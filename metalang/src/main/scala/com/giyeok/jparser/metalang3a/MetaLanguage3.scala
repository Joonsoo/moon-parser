package com.giyeok.jparser.metalang3a

import com.giyeok.jparser.ParsingErrors.WithLocation
import com.giyeok.jparser.examples.MetaLang3Example
import com.giyeok.jparser.examples.MetaLang3Example.CorrectExample
import com.giyeok.jparser.metalang3a.Type._
import com.giyeok.jparser.metalang3a.ValuefyExpr.UnrollChoices
import com.giyeok.jparser.metalang3a.codegen.ScalaCodeGen
import com.giyeok.jparser.metalang3a.generated.MetaLang3Ast
import com.giyeok.jparser.utils.FileUtil.writeFile
import com.giyeok.jparser.{Grammar, NGrammar, ParsingErrors}

import java.io.File

object MetaLanguage3 {

  case class IllegalGrammar(msg: String) extends Exception(msg)

  def check(cond: Boolean, errorMessage: String): Unit = {
    if (!cond) throw IllegalGrammar(errorMessage)
  }

  def parseGrammar(grammar: String): MetaLang3Ast.Grammar = MetaLang3Ast.parseAst(grammar) match {
    case Left(value) => value
    case Right(value) =>
      val errorMsg = value match {
        case withLocation: WithLocation =>
          val lines = grammar.substring(0, withLocation.location).count(_ == '\n') + 1
          val lastNewLine = grammar.lastIndexOf('\n', withLocation.location)
          val colNum = withLocation.location - lastNewLine + 1
          s"${value.msg} at $lines:$colNum"
        case _ => value.msg
      }
      throw IllegalGrammar(errorMsg)
  }

  case class ProcessedGrammar(grammar: Grammar, ngrammar: NGrammar, startNonterminalName: String,
                              nonterminalTypes: Map[String, Type], nonterminalValuefyExprs: Map[String, UnrollChoices],
                              rawClassRelations: ClassRelationCollector, classParamTypes: Map[String, List[(String, Type)]],
                              shortenedEnumTypesMap: Map[Int, String], enumValuesMap: Map[String, Set[String]],
                              errors: CollectedErrors) {
    val typeInferer = new TypeInferer(startNonterminalName, nonterminalTypes)
    val classRelations: ClassRelationCollector =
      if (errors.isClear) rawClassRelations.removeDuplicateEdges() else null

    def isSubtypeOf(superType: Type, subType: Type): Boolean = (superType, subType) match {
      case (ClassType(superClass), ClassType(subClass)) => classRelations.reachableBetween(superClass, subClass)
      case (OptionalOf(superOpt), OptionalOf(subOpt)) => isSubtypeOf(superOpt, subOpt)
      case (ArrayOf(superElem), ArrayOf(subElem)) => isSubtypeOf(superElem, subElem)
      case (EnumType(superEnumName), EnumType(subEnumName)) => superEnumName == subEnumName
      case (EnumType(enumName), UnspecifiedEnumType(uniqueId)) => shortenedEnumTypesMap(uniqueId) == enumName
      case (UnspecifiedEnumType(uniqueId), EnumType(enumName)) => shortenedEnumTypesMap(uniqueId) == enumName
      case (UnspecifiedEnumType(superEnumId), UnspecifiedEnumType(subEnumId)) =>
        shortenedEnumTypesMap(superEnumId) == shortenedEnumTypesMap(subEnumId)
      case (Type.AnyType, _) => true
      case (_, Type.NothingType) => true
      case (superType, subType) => superType == subType
    }

    // TODO UnionOf(UnspecifiedEnumType(1), UnspecifiedEnumType(2))가 들어왔는데 UnspecifiedEnumType(1)과 2가 같은 enum인 경우 Nothing이 반환됨. 고쳐야 함
    def reduceUnionType(unionType: UnionOf): Type = {
      val types = unionType.types
      // TODO types가 모두 class type인 경우 공통 supertype이면서 그 supertype의 모든 subtype이 types와 일치하면 그 supertype을 반환
      val reducedTypes = types.filterNot { subType =>
        // (types - subType)에 subType보다 super type인 타입이 존재하면 subType은 없어도 됨
        (types - subType).exists { superType => isSubtypeOf(superType, subType) }
      }
      Type.unifyTypes(reducedTypes)
    }

    def validated(): ProcessedGrammar = if (!errors.isClear) this else {
      // validate하기 전에 이미 오류가 있었으면 더이상 validate할 필요가 없음
      // 여기까지 얻어진 정보에서 오류를 찾아서 오류가 있으면 `errors`에 추가해서 반환. 오류가 없으면 그대로 반환
      // - function call에서
      //   - ispresent의 인자는 하나여야 하고, 그 타입은 array, optional, string, node 중 하나여야 함
      //   - isempty도 마찬가지
      //   - str은 각 인자가 node, bool, char, string, 혹은 이 네가지 타입의 어레이(혹은 그 어레이의 어레이)여야 함
      // - 연산자에서
      //   - !expr에서 expr은 bool 타입이어야 함
      //   - A&&B와 A||B에서 A와 B는 bool 타입이어야 함
      //   - A==B와 A!=B에서 A와 B의 타입이 consistent해야 함(A가 B의 서브타입이거나 B가 A의 서브타입이면 될듯?)
      //   - A?B:C 에서 A는 bool이어야 하고 B와 C의 타입은 consistent(B가 C의 서브타입이거나 C가 B의 서브타입이거나)
      // - 타입 관계에서
      //   - abstract로도 concrete로도 사용되는 클래스(다른 클래스의 super 클래스이면서 어딘가에서 생성자도 호출되는 클래스)
      this
    }
  }

  def analyzeGrammar(transformer: GrammarTransformer, grammar: Grammar, ngrammar: NGrammar)
                    (implicit errorCollector: ErrorCollector): ProcessedGrammar = {
    val inferredTypeCollector = new InferredTypeCollector(
      transformer.startNonterminalName(), transformer.classInfo, grammar.rules.keySet, transformer.nonterminalInfo)

    var counter = 0
    while (errorCollector.isClear && inferredTypeCollector.tryInference()) {
      counter += 1
      if (counter > 5) {
        println(s"try inference for $counter times...")
      }
    }

    if (!inferredTypeCollector.isComplete) {
      errorCollector.addError("Incomplete type info")
    }

    inferredTypeCollector.typeRelations.classRelations.checkCycle()

    val classParamTypes = inferredTypeCollector.classParamTypes.map { pair =>
      val (className, paramTypes) = pair
      val paramNames = transformer.classInfo.classParamSpecs(className).params.map(_.name)
      className -> paramNames.zip(paramTypes)
    }.toMap


    val enumsMap = inferredTypeCollector.typeRelations.enumRelations.toUnspecifiedEnumMap
    var enumValues = transformer.classInfo.canonicalEnumValues
    enumsMap.foreach(pair =>
      enumValues += (pair._2 -> (enumValues.getOrElse(pair._2, Set()) ++
        transformer.classInfo.shortenedEnumValues.getOrElse(pair._1, Set())))
    )

    ProcessedGrammar(grammar, ngrammar, transformer.startNonterminalName(),
      inferredTypeCollector.nonterminalTypes, transformer.nonterminalValuefyExprs,
      inferredTypeCollector.typeRelations.classRelations, classParamTypes,
      enumsMap, enumValues,
      errorCollector.collectedErrors
    ).validated()
  }

  def transformGrammar(grammarDef: MetaLang3Ast.Grammar, grammarName: String)
                      (implicit errorCollector: ErrorCollector): (GrammarTransformer, Grammar, NGrammar) = {
    val transformer = new GrammarTransformer(grammarDef, errorCollector)
    val grammar = transformer.grammar(grammarName)
    val ngrammar = NGrammar.fromGrammar(grammar)

    (transformer, grammar, ngrammar)
  }

  def analyzeGrammar(grammarDef: MetaLang3Ast.Grammar, grammarName: String): ProcessedGrammar = {
    implicit val errorCollector: ErrorCollector = new ErrorCollector()
    val (transformer, grammar, ngrammar) = transformGrammar(grammarDef, grammarName)

    analyzeGrammar(transformer, grammar, ngrammar)
  }

  def analyzeGrammar(grammarDefinition: String, grammarName: String = "GeneratedGrammar"): ProcessedGrammar =
    analyzeGrammar(parseGrammar(grammarDefinition), grammarName)

  def generateScalaParserCode(grammar: String, className: String, packageName: String,
                              mainFuncExamples: Option[List[String]] = None,
                              options: ScalaCodeGen.Options = ScalaCodeGen.Options()): (ProcessedGrammar, String) = {
    val analysis = analyzeGrammar(grammar, className)
    if (!analysis.errors.isClear) {
      throw new Exception(analysis.errors.toString)
    }

    val codegen = new ScalaCodeGen(analysis, options)

    (analysis, s"package $packageName\n\n" + codegen.generateParser(className, mainFuncExamples))
  }

  def writeScalaParserCode(grammar: String, className: String, packageName: String, targetDir: File,
                           mainFuncExamples: Option[List[String]] = None,
                           options: ScalaCodeGen.Options = ScalaCodeGen.Options()): ProcessedGrammar = {
    val (analysis, generatedCode) = generateScalaParserCode(grammar, className, packageName, mainFuncExamples, options)

    val filePath = new File(targetDir, s"${packageName.split('.').mkString("/")}/$className.scala")

    writeFile(filePath, generatedCode)

    analysis
  }

  def testExample(example: MetaLang3Example): Unit = {
    println(example.grammar)
    val analysis = analyzeGrammar(example.grammar, example.name)
    if (!analysis.errors.isClear) {
      throw new Exception(analysis.errors.toString)
    }
    val valuefyExprSimulator = new ValuefyExprSimulator(analysis.ngrammar, analysis.startNonterminalName, analysis.nonterminalValuefyExprs, analysis.shortenedEnumTypesMap)
    val analysisPrinter = new AnalysisPrinter(analysis.ngrammar, valuefyExprSimulator.startValuefyExpr, analysis.nonterminalValuefyExprs, analysis.shortenedEnumTypesMap)

    analysis.nonterminalTypes.foreach { p =>
      println(s"Nonterm `${p._1}` = ${Type.readableNameOf(p._2)}")
    }
    AnalysisPrinter.printClassHierarchy(analysis.rawClassRelations.toHierarchy)
    val classHierarchy = analysis.classRelations.toHierarchy
    AnalysisPrinter.printClassHierarchy(classHierarchy)
    println(s"Enum Values: ${analysis.enumValuesMap}")
    println(s"Shortened enums: ${analysis.shortenedEnumTypesMap}")
    analysis.classParamTypes.foreach(pair =>
      // TODO supers
      AnalysisPrinter.printClassDef(classHierarchy, pair._1, pair._2)
    )
    analysisPrinter.printValuefyStructure()

    if (!analysis.errors.isClear) {
      throw IllegalGrammar(s"Errors: ${analysis.errors.errors}")
    }
    example.correctExamplesWithResults.foreach { example =>
      val CorrectExample(input, expectedResult) = example
      valuefyExprSimulator.parse(input) match {
        case Left(parsed) =>
          println(s"== Input: $input")
          AnalysisPrinter.printNodeStructure(parsed)
          val valuefied = valuefyExprSimulator.valuefyStart(parsed)
          println(s"Value: ${valuefied.prettyPrint()}")
          // println(valuefied.detailPrint())
          expectedResult.foreach(someExpectedResult =>
            check(valuefied.prettyPrint() == someExpectedResult,
              s"Valuefy result mismatch, expected=$someExpectedResult, actual=${valuefied.prettyPrint()}"))
        case Right(error) =>
          println(error)
      }
    }
  }
}
