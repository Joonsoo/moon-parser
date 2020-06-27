package com.giyeok.jparser.metalang3a

import com.giyeok.jparser.NGrammar
import com.giyeok.jparser.examples.MetaLang3Example
import com.giyeok.jparser.metalang2.generated.MetaGrammar3Ast
import com.giyeok.jparser.metalang3a.ValuefyExpr.UnrollChoices

object MetaLanguage3 {

    case class IllegalGrammar(msg: String) extends Exception(msg)

    def check(cond: Boolean, errorMessage: String): Unit = {
        if (!cond) throw IllegalGrammar(errorMessage)
    }

    def parseGrammar(grammar: String): MetaGrammar3Ast.Grammar = MetaGrammar3Ast.parseAst(grammar) match {
        case Left(value) => value
        case Right(value) => throw IllegalGrammar(value.msg)
    }

    case class ProcessedGrammar(ngrammar: NGrammar, startNonterminalName: String, nonterminalValuefyExprs: Map[String, UnrollChoices],
                                classRelations: ClassRelationCollector, classParamTypes: Map[String, List[(String, Type)]])

    def analyzeGrammar(grammarDef: MetaGrammar3Ast.Grammar, grammarName: String): ProcessedGrammar = {
        val transformer = new GrammarTransformer(grammarDef)
        val grammar = transformer.grammar(grammarName)

        val inferredTypeCollector = new InferredTypeCollector(transformer.startNonterminalName(), transformer.classInfo, grammar.rules.keySet, transformer.nonterminalInfo)
        while (inferredTypeCollector.tryInference()) {
            println("tryInference")
        }

        if (!inferredTypeCollector.isComplete()) {
            throw IllegalGrammar("incomplete type info")
        }

        if (inferredTypeCollector.classRelations.hasCycle) {
            // TODO error handling
            throw IllegalGrammar("cyclic class relation")
        }

        val typeInferer = new TypeInferer(transformer.startNonterminalName(), transformer.nonterminalInfo.specifiedTypes)
        transformer.classInfo.classConstructCalls.foreach { calls =>
            calls._2.foreach { call =>
                println(call)
                call.foreach { param =>
                    println(typeInferer.typeOfValuefyExpr(param))
                }
            }
        }

        val classParamTypes = inferredTypeCollector.classParamTypes.map { pair =>
            val (className, paramTypes) = pair
            val paramNames = transformer.classInfo.classParamSpecs(className).params.map(_.name)
            className -> paramNames.zip(paramTypes)
        }.toMap

        val ngrammar = NGrammar.fromGrammar(grammar)
        ProcessedGrammar(ngrammar, transformer.startNonterminalName(), transformer.nonterminalValuefyExprs,
            inferredTypeCollector.classRelations, classParamTypes)
    }

    def analyzeGrammar(grammarDefinition: String, grammarName: String = "GeneratedGrammar"): ProcessedGrammar =
        analyzeGrammar(parseGrammar(grammarDefinition), grammarName)

    def main(args: Array[String]): Unit = {
        def testExample(example: MetaLang3Example): Unit = {
            println(example.grammar)
            val analysis = analyzeGrammar(example.grammar, example.name)
            val valuefyExprSimulator = new ValuefyExprSimulator(analysis.ngrammar, analysis.startNonterminalName, analysis.nonterminalValuefyExprs)
            val analysisPrinter = new AnalysisPrinter(valuefyExprSimulator.startValuefyExpr, analysis.nonterminalValuefyExprs)

            val classHierarchy = analysis.classRelations.toHierarchy
            analysisPrinter.printClassHierarchy(classHierarchy)
            analysis.classParamTypes.foreach(pair =>
                // TODO supers
                analysisPrinter.printClassDef(pair._1, pair._2)
            )
            example.correctExamples.foreach { exampleSource =>
                val parsed = valuefyExprSimulator.parse(exampleSource).left.get
                analysisPrinter.printNodeStructure(parsed)
                analysisPrinter.printValuefyStructure()
                println(valuefyExprSimulator.valuefy(parsed).prettyPrint())
            }
        }

        val ex1 = MetaLang3Example("Simple test",
            """A = B&X {MyClass(value=$0, qs=[])} | B&X Q {MyClass(value=$0, qs=$1)}
              |B = C 'b'
              |C = 'c' D
              |D = 'd' | #
              |X = 'c' 'd'* 'b'
              |Q = 'q'+ {QValue(value="hello")}
              |""".stripMargin)
            .example("cb")
            .example("cdb")
            .example("cbqq")
        //        testExample(ex1)
        val ex2 = MetaLang3Example("Simple",
            """A = ('b' Y 'd') 'x' {A(val=$0$1, raw=$0\\$1, raw1=\\$0)}
              |Y = 'y' {Y(value=true)}
              |""".stripMargin)
            .example("bydx")
        //        testExample(ex2)
        val ex3 = MetaLang3Example("BinOp",
            """A = B ' ' C {ClassA(value=str($0) + str($2))}
              |B = 'a'
              |C = 'b'
              |""".stripMargin)
            .example("a b")
        //        testExample(ex3)
        val ex4 = MetaLang3Example("Nonterm type inference",
            """expression: Expression = term
              |    | expression WS '+' WS term {BinOp(op=$2, lhs:Expression=$0, rhs=$4)}
              |term: Term = factor
              |    | term WS '*' WS factor {BinOp($2, $0, $4)}
              |factor: Factor = number
              |    | variable
              |    | '(' WS expression WS ')' {Paren(expr=$2)}
              |number: Number = '0' {Integer(value=[$0])}
              |    | '1-9' '0-9'* {Integer([$0] + $1)}
              |variable = <'A-Za-z'+> {Variable(name=$0)}
              |WS = ' '*
              |""".stripMargin)
            .example("1+2")
        testExample(ex4)
    }
}
