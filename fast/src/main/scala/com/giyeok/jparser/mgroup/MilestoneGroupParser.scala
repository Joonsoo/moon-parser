package com.giyeok.jparser.mgroup

import com.giyeok.jparser.Inputs.{Input, TermGroupDesc}
import com.giyeok.jparser.ParsingErrors.ParsingError
import com.giyeok.jparser.mgroup.MilestoneGroupParser.reconstructParseTree
import com.giyeok.jparser.nparser.AcceptCondition.{AcceptCondition, Always, And, Exists, Never, NotExists, OnlyIf, Or, Unless, conjunct, disjunct}
import com.giyeok.jparser.{Inputs, ParseForest, ParsingErrors}

class MilestoneGroupParser(val parserData: MilestoneGroupParserData, val verbose: Boolean = false) {
  def initialCtx: MilestoneGroupParserContext = MilestoneGroupParserContext(0,
    List(MilestoneGroupPath(None, parserData.startMilestoneGroup, 0, List(Always))), List())

  def materializeEdgeActionAcceptCondition(acceptCondition: AcceptCondition,
                                           grandParentGen: Int, parentGen: Int, tipGen: Int, nextGen: Int): AcceptCondition = acceptCondition

  class Proceeder(ctx: MilestoneGroupParserContext) {
    private val nextGen = ctx.gen + 1

    def applyEdgeAction(path: MilestoneGroupPath): List[MilestoneGroupPath] = path.parent match {
      case Some(parent) =>
        val edgeAction = parserData.edgeActions(parent.milestoneGroup -> path.milestoneGroup)
        val appending = edgeAction.appending.map { appending =>
          val newParent = replaceTip(parent, appending.parentReplacement)
          MilestoneGroupPath(Some(newParent), appending.appendingMGroup, nextGen,
            succeedAcceptConditions(path.acceptConditionSlots, appending.acceptConditions))
        }
        val chaining = edgeAction.parentProgress.toList.flatMap { parentProgress =>
          applyEdgeAction(applyTipProgress(parent, parentProgress))
        }
        appending.toList ++ chaining
      case None => List()
    }

    def replaceTip(path: MilestoneGroupPath, replacementOpt: Option[StepReplacement]): MilestoneGroupPath = replacementOpt match {
      case Some(replacement) => MilestoneGroupPath(parent = path.parent, milestoneGroup = replacement.mgroup,
        gen = path.gen, acceptConditionSlots = replacement.succeedingAcceptConditionSlots.map(path.acceptConditionSlots))
      case None => path
    }

    def succeedAcceptConditions(prevSlots: List[AcceptCondition], successions: List[AcceptConditionSuccession]): List[AcceptCondition] = {
      successions.map { succ =>
        disjunct(prevSlots(succ.succeedingSlot),
          materializeEdgeActionAcceptCondition(succ.newCondition, ???, ???, ???, ???))
      }
    }

    def applyTipProgress(path: MilestoneGroupPath, tipProgress: TipProgress): MilestoneGroupPath =
      MilestoneGroupPath(parent = path.parent, milestoneGroup = tipProgress.tipReplacement, gen = path.gen,
        acceptConditionSlots = succeedAcceptConditions(path.acceptConditionSlots, tipProgress.acceptConditions))

    def proceed(input: Inputs.Input): Either[MilestoneGroupParserContext, ParsingError] = {
      val newPaths = ctx.paths.flatMap { path =>
        // TODO path에 drop action 적용(parserData.milestoneDropActions(path.milestoneGroup))
        val termActions = parserData.termActions(path.milestoneGroup)
        termActions.find(_._1.contains(input)) match {
          case Some((_, termAction)) =>
            val appended = termAction.appendAction.map { appending =>
              // appending이 있으면
              val newTip = replaceTip(path, appending.tipReplacement)
              MilestoneGroupPath(Some(newTip), appending.appendingMGroup, nextGen, appending.acceptConditions)
            }
            // (path.parent->newTip) 엣지에 대해서 edgeAction 적용 시작
            val progressed = termAction.tipProgress.toList.flatMap { tipProgress =>
              applyEdgeAction(applyTipProgress(path, tipProgress))
            }
            appended.toList ++ progressed
          case None => List()
        }
      }
      if (newPaths.isEmpty) Right(ParsingErrors.UnexpectedInputByTermGroups(input, ctx.expectingTerminals(parserData), ctx.gen))
      else Left(MilestoneGroupParserContext(nextGen, newPaths, List()))
    }
  }

  def proceed(ctx: MilestoneGroupParserContext, input: Inputs.Input): Either[MilestoneGroupParserContext, ParsingError] =
    new Proceeder(ctx).proceed(input)

  def parse(inputSeq: Seq[Inputs.Input]): Either[MilestoneGroupParserContext, ParsingError] = {
    if (verbose) {
      println("=== initial")
      initialCtx.paths.foreach(t => println(t.prettyString))
    }
    inputSeq.foldLeft[Either[MilestoneGroupParserContext, ParsingError]](Left(initialCtx)) { (m, nextInput) =>
      m match {
        case Left(currCtx) =>
          if (verbose) {
            println(s"=== ${currCtx.gen} $nextInput")
          }
          proceed(currCtx, nextInput)
        case error => error
      }
    }
  }

  def parse(input: String): Either[MilestoneGroupParserContext, ParsingError] = parse(Inputs.fromString(input))

  def parseAndReconstructToForest(inputSeq: Seq[Inputs.Input]): Either[ParseForest, ParsingError] = {
    parse(inputSeq) match {
      case Left(finalCtx) =>
        reconstructParseTree(parserData, finalCtx, inputSeq) match {
          case Some(forest) => Left(forest)
          case None => Right(ParsingErrors.UnexpectedEOFByTermGroups(finalCtx.expectingTerminals(parserData), finalCtx.gen))
        }
      case Right(error) => Right(error)
    }
  }

  def parseAndReconstructToForest(input: String): Either[ParseForest, ParsingError] =
    parseAndReconstructToForest(Inputs.fromString(input))
}

object MilestoneGroupParser {
  def reconstructParseTree(parserData: MilestoneGroupParserData, finalCtx: MilestoneGroupParserContext, input: Seq[Input]): Option[ParseForest] = {
    ???
  }
}

case class MilestoneGroupParserContext(gen: Int, paths: List[MilestoneGroupPath], genProgressHistory: List[GenProgress]) {
  def expectingTerminals(parserData: MilestoneGroupParserData): Set[TermGroupDesc] = ???
}

case class MilestoneGroupPath(parent: Option[MilestoneGroupPath], milestoneGroup: Int, gen: Int, acceptConditionSlots: List[AcceptCondition]) {
  private def myself: String = s"($milestoneGroup $gen)"

  def prettyString: String = parent match {
    case Some(value) => s"${value.prettyString} $myself"
    case None => myself
  }
}

case class GenProgress()
