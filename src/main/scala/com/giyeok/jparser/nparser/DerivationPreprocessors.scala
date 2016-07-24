package com.giyeok.jparser.nparser

import com.giyeok.jparser.nparser.DerivationPreprocessor.Preprocessed
import com.giyeok.jparser.nparser.ParsingContext._
import com.giyeok.jparser.nparser.EligCondition.Condition
import com.giyeok.jparser.nparser.EligCondition.True
import com.giyeok.jparser.Inputs.TermGroupDesc
import com.giyeok.jparser.Inputs.Input
import scala.annotation.tailrec

class OnDemandDerivationPreprocessor(val grammar: NGrammar) extends DerivationPreprocessor with ParsingTasks {
    private val symbolDerivations = scala.collection.mutable.Map[Int, Preprocessed]()
    private val sequenceDerivations = scala.collection.mutable.Map[(Int, Int), Preprocessed]()

    private val symbolTermNodes = scala.collection.mutable.Map[Int, Set[SymbolNode]]()
    private val sequenceTermNodes = scala.collection.mutable.Map[(Int, Int), Set[SymbolNode]]()

    @tailrec private def recNoBase(baseNode: Node, nextGen: Int, tasks: List[Task], cc: Preprocessed): Preprocessed =
        tasks match {
            case FinishTask(`baseNode`, condition, lastSymbol) +: rest =>
                recNoBase(baseNode, nextGen, rest, cc.addBaseFinish(condition, lastSymbol))
            case ProgressTask(`baseNode`, condition) +: rest =>
                recNoBase(baseNode, nextGen, rest, cc.addBaseProgress(condition))
            case task +: rest =>
                val (newContext, newTasks) = process(nextGen, task, cc.context)
                recNoBase(baseNode, nextGen, newTasks ++: rest, cc.updateContext(newContext))
            case List() => cc
        }

    def symbolDerivationOf(symbolId: Int): Preprocessed = {
        symbolDerivations get symbolId match {
            case Some(preprocessed) => preprocessed
            case None =>
                val baseNode = SymbolNode(symbolId, -1)
                val initialPreprocessed = Preprocessed(baseNode, Context(Graph(Set(baseNode), Set()), Results(), Results()), Seq(), Seq())
                val preprocessed = recNoBase(baseNode, 0, List(DeriveTask(baseNode)), initialPreprocessed)
                symbolDerivations(symbolId) = preprocessed
                preprocessed
        }
    }
    def sequenceDerivationOf(sequenceId: Int, pointer: Int): Preprocessed = {
        sequenceDerivations get (sequenceId, pointer) match {
            case Some(baseNodeAndDerivation) => baseNodeAndDerivation
            case None =>
                val baseNode = SequenceNode(sequenceId, pointer, -1, -1)
                val initialPreprocessed = Preprocessed(baseNode, Context(Graph(Set(baseNode), Set()), Results(baseNode -> Set[Condition]()), Results()), Seq(), Seq())
                val preprocessed = recNoBase(baseNode, 0, List(DeriveTask(baseNode)), initialPreprocessed)
                sequenceDerivations((sequenceId, pointer)) = preprocessed
                preprocessed
        }
    }

    def symbolTermNodesOf(symbolId: Int): Set[SymbolNode] = {
        symbolTermNodes get symbolId match {
            case Some(termNodes) => termNodes
            case None =>
                val termNodes: Set[SymbolNode] = symbolDerivationOf(symbolId).context.graph.nodes collect {
                    case node @ SymbolNode(symbolId, _) if grammar.nsymbols(symbolId).isInstanceOf[NGrammar.Terminal] => node
                }
                symbolTermNodes(symbolId) = termNodes
                termNodes
        }
    }
    def sequenceTermNodesOf(sequenceId: Int, pointer: Int): Set[SymbolNode] = {
        sequenceTermNodes get (sequenceId, pointer) match {
            case Some(termNodes) => termNodes
            case None =>
                val termNodes: Set[SymbolNode] = sequenceDerivationOf(sequenceId, pointer).context.graph.nodes collect {
                    case node @ SymbolNode(symbolId, beginGen) if grammar.nsymbols(symbolId).isInstanceOf[NGrammar.Terminal] => node
                }
                sequenceTermNodes((sequenceId, pointer)) = termNodes
                termNodes
        }
    }
}

class OnDemandSlicedDerivationPreprocessor(grammar: NGrammar) extends OnDemandDerivationPreprocessor(grammar) with SlicedDerivationPreprocessor {
    // TODO slice가 TermGroupDesc->Preprocessed가 아니고 Preprocessed + finish 혹은 progress task를 만들기 위한 정보가 되어야 할듯
    private val symbolSliced = scala.collection.mutable.Map[Int, Map[TermGroupDesc, (Preprocessed, Set[SequenceNode])]]()
    private val sequenceSliced = scala.collection.mutable.Map[(Int, Int), Map[TermGroupDesc, (Preprocessed, Set[SequenceNode])]]()

    @tailrec private def recNoBaseNoDerive(baseNode: Node, nextGen: Int, tasks: List[Task], cc: Preprocessed, deriveTips: Set[SequenceNode]): (Preprocessed, Set[SequenceNode]) =
        tasks match {
            case DeriveTask(deriveTip: SequenceNode) +: rest =>
                // context에 deriveTip의 finish task 추가
                val preprocessed = derivationOf(deriveTip)
                assert(preprocessed.baseFinishes.isEmpty)
                val immediateProgresses = preprocessed.baseProgresses map { condition => ProgressTask(deriveTip, condition.shiftGen(nextGen)) }
                // recNoBaseNoDerive(nextGen, immediateProgresses ++: rest, context.updateFinishes(_.merge(preprocessed.context.finishes.shiftGen(nextGen))), deriveTips + deriveTip)
                ???
            case FinishTask(`baseNode`, condition, lastSymbol) +: rest =>
                recNoBaseNoDerive(baseNode, nextGen, rest, cc.addBaseFinish(condition, lastSymbol), deriveTips)
            case ProgressTask(`baseNode`, condition) +: rest =>
                recNoBaseNoDerive(baseNode, nextGen, rest, cc.addBaseProgress(condition), deriveTips)
            case task +: rest =>
                // assert(!task.isInstanceOf[DeriveTask])
                val (newContext, newTasks) = process(nextGen, task, cc.context)
                recNoBaseNoDerive(baseNode, nextGen, newTasks ++: rest, cc.updateContext(newContext), deriveTips)
            case List() =>
                (cc, deriveTips)
        }

    private def slice(derivation: Preprocessed, termNodes: Set[SymbolNode]): Map[TermGroupDesc, (Preprocessed, Set[SequenceNode])] = {
        val terminals = termNodes map { node => grammar.nsymbols(node.symbolId).asInstanceOf[NGrammar.Terminal].symbol }
        val termGroups = termGroupsOf(terminals)
        (termGroups map { termGroup =>
            val finishables = finishableTermNodes(derivation.context, 0, termGroup)
            val finishTasks = finishables.toList map { FinishTask(_, True, None) }
            val cc = Preprocessed(derivation.baseNode, derivation.context.emptyFinishes, Seq(), Seq())
            val sliced = recNoBaseNoDerive(derivation.baseNode, 1, finishTasks, cc, Set())
            // TODO trim
            // TODO baseNode에 대한 derive도 모아야됨 (recNoBase면 안됨)
            (termGroup -> sliced)
        }).toMap
        ???
    }
    def symbolSliceOf(symbolId: Int): Map[TermGroupDesc, (Preprocessed, Set[SequenceNode])] = {
        symbolSliced get symbolId match {
            case Some(slicedMap) => slicedMap
            case None =>
                val slicedMap = slice(symbolDerivationOf(symbolId), symbolTermNodesOf(symbolId))
                symbolSliced(symbolId) = slicedMap
                slicedMap
        }
    }
    def sequenceSliceOf(sequenceId: Int, pointer: Int): Map[TermGroupDesc, (Preprocessed, Set[SequenceNode])] = {
        sequenceSliced get (sequenceId, pointer) match {
            case Some(slicedMap) => slicedMap
            case None =>
                val slicedMap = slice(sequenceDerivationOf(sequenceId, pointer), sequenceTermNodesOf(sequenceId, pointer))
                sequenceSliced((sequenceId, pointer)) = slicedMap
                slicedMap
        }
    }
}
