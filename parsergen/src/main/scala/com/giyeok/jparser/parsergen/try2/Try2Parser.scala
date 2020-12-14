package com.giyeok.jparser.parsergen.try2

import com.giyeok.jparser.Inputs
import com.giyeok.jparser.metalang2.generated.ExpressionGrammar
import com.giyeok.jparser.metalang3a.generated.ArrayExprAst
import com.giyeok.jparser.nparser.AcceptCondition
import com.giyeok.jparser.nparser.AcceptCondition.{AcceptCondition, Always}
import com.giyeok.jparser.parsergen.try2.Try2.{KernelTemplate, PrecomputedParserData, TasksSummary}

object Try2Parser {
  def main(args: Array[String]): Unit = {
    //    val parserData = Try2.precomputedParserData(ExpressionGrammar.ngrammar)
    //    new Try2Parser(parserData).parse("1*2+34")
    val parserData = Try2.precomputedParserData(ArrayExprAst.ngrammar)
    new Try2Parser(parserData).parse("[a,a,a]")
    println(parserData)
  }
}

class Try2Parser(val parserData: PrecomputedParserData) {
  def initialCtx: Try2ParserContext = Try2ParserContext(List(Milestone(None, parserData.grammar.startSymbol, 0, 0, Always)))

  def parse(input: String) = {
    val inputSeq = Inputs.fromString(input)

    println("=== initial")
    initialCtx.tips.foreach(t => println(t.prettyString))
    val result = inputSeq.zipWithIndex.foldLeft(initialCtx) { (m, i) =>
      val (nextInput, gen0) = i
      val gen = gen0 + 1
      val next = proceed(m, gen, nextInput)
      println(s"=== $gen $nextInput")
      next.tips.foreach(t => println(t.prettyString))
      next
    }
    println(result)
  }

  private def progressTip(tip: Milestone, gen: Int, acceptConditions: List[AcceptCondition]): List[Milestone] =
    acceptConditions.flatMap { condition =>
      // (tip.parent-tip) 사이의 엣지에 대한 edge action 실행
      tip.parent match {
        case Some(parent) =>
          val edgeAction = parserData.edgeProgressActions((parent.kernelTemplate, tip.kernelTemplate))
          // tip은 지워지고 tip.parent - edgeAction.appendingMilestones 가 추가됨
          val appended = edgeAction.appendingMilestones.map(appending =>
            Milestone(Some(parent), appending._1.symbolId, appending._1.pointer, gen,
              AcceptCondition.conjunct(tip.acceptCondition, appending._2, condition))
          )
          // edgeAction.startNodeProgressConditions에 대해 위 과정 반복 수행
          val propagated = progressTip(parent, gen, edgeAction.startNodeProgressConditions)
          appended ++ propagated
        case None =>
          // 파싱 종료
          // TODO 어떻게 처리하지?
          List()
      }
    }

  def proceed(ctx: Try2ParserContext, gen: Int, input: Inputs.Input): Try2ParserContext = {
    val newTips = ctx.tips.flatMap { tip =>
      val termActions = parserData.termActions(tip.kernelTemplate)
      termActions.find(_._1.contains(input)) match {
        case Some((_, action)) =>
          // action.appendingMilestones를 뒤에 덧붙인다
          val appended = action.appendingMilestones.map { appending =>
            val (kernelTemplate, acceptCondition) = appending
            Milestone(Some(tip), kernelTemplate.symbolId, kernelTemplate.pointer, gen,
              AcceptCondition.conjunct(tip.acceptCondition, acceptCondition))
          }
          // action.startNodeProgressConditions가 비어있지 않으면 tip을 progress 시킨다
          val reduced = progressTip(tip, gen, action.startNodeProgressConditions)
          appended ++ reduced
        case None => List()
      }
    }
    Try2ParserContext(newTips)
  }
}

case class Milestone(parent: Option[Milestone], symbolId: Int, pointer: Int, gen: Int, acceptCondition: AcceptCondition) {
  def kernelTemplate: KernelTemplate = KernelTemplate(symbolId, pointer)

  private def myself = s"($symbolId $pointer $gen ${acceptCondition})"

  def prettyString: String = parent match {
    case Some(value) => s"${value.prettyString} $myself"
    case None => myself
  }
}

sealed trait GenAction

case class TermAction(tipIndex: Int, summary: TasksSummary) extends GenAction

// TODO edge action - 체인 관계를 어떻게..?

case class Try2ParserContext(tips: List[Milestone])
