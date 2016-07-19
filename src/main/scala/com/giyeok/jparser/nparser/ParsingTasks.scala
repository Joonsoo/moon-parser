package com.giyeok.jparser.nparser

import ParsingContext._
import EligCondition._
import NGrammar._
import com.giyeok.jparser.Inputs

trait ParsingTasks {
    val grammar: NGrammar

    sealed trait Task
    case class DeriveTask(node: Node) extends Task
    case class FinishTask(node: Node, condition: Condition, lastSymbol: Option[Int]) extends Task
    case class ProgressTask(node: SequenceNode, condition: Condition) extends Task

    def deriveTask(nextGen: Int, task: DeriveTask, cc: Context): (Context, Seq[Task]) = {
        val DeriveTask(startNode) = task

        def nodeOf(symbolId: Int): Node =
            grammar.symbolOf(symbolId) match {
                case _: NAtomicSymbol => SymbolNode(symbolId, nextGen)
                case _: Sequence => SequenceNode(symbolId, 0, nextGen, nextGen)
            }
        def derive(symbolIds: Set[Int]): (Context, Seq[Task]) = {
            // (symbolIds에 해당하는 노드) + (startNode에서 symbolIds의 노드로 가는 엣지) 추가
            val destNodes = symbolIds map { nodeOf _ }
            val newNodes = destNodes -- cc.graph.nodes
            // 새로 추가된 노드에 대한 DeriveTask 추가
            val newDeriveTasks = newNodes.toSeq map { DeriveTask(_) }
            // destNodes 중 cc.finishes에 있으면 finish 추가
            val immediateFinishTasks = destNodes.toSeq flatMap { destNode =>
                val destSymbolIdOpt = destNode match {
                    case SymbolNode(symbolId, _) => Some(symbolId)
                    case _: SequenceNode => None
                }
                cc.finishes.of(destNode).getOrElse(Set()) map { condition =>
                    startNode match {
                        case startNode: SymbolNode => FinishTask(startNode, condition, destSymbolIdOpt)
                        case startNode: SequenceNode => ProgressTask(startNode, condition)
                    }
                }
            }

            val newGraph = destNodes.foldLeft(cc.graph) { (graph, node) =>
                graph.addNode(node).addEdge(SimpleEdge(startNode, node))
            }
            val newCC = (newNodes collect { case n: SequenceNode => n }).foldLeft(cc.updateGraph(newGraph)) { (context, newSeqNode) =>
                context.updateProgresses(_.update(newSeqNode, True))
            }
            (newCC, newDeriveTasks ++ immediateFinishTasks)
        }
        startNode match {
            case SymbolNode(symbolId, _) =>
                grammar.nsymbols(symbolId) match {
                    case _: Terminal => (cc, Seq()) // nothing to do

                    case derivable: NSimpleDerivable => derive(derivable.produces)

                    case Join(_, body, join) =>
                        val bodyNode = nodeOf(body)
                        val joinNode = nodeOf(join)
                        val newDeriveTasks = (Set(bodyNode, joinNode) -- cc.graph.nodes).toSeq map { DeriveTask(_) }
                        val immediateFinishTasks = (cc.finishes.of(bodyNode), cc.finishes.of(joinNode)) match {
                            case (Some(bodyConditions), Some(joinConditions)) =>
                                bodyConditions.toSeq flatMap { b =>
                                    joinConditions map { j =>
                                        FinishTask(startNode, conjunct(b, j), None)
                                    }
                                }
                            case _ => Seq()
                        }
                        (cc.updateGraph(_.addNode(bodyNode).addNode(joinNode).addEdge(JoinEdge(startNode, bodyNode, joinNode))), immediateFinishTasks ++ newDeriveTasks)

                    case lookahead: NLookaheadSymbol =>
                        val lookaheadNode = nodeOf(lookahead.lookahead)
                        val newDeriveTask = if (!(cc.graph.nodes contains lookaheadNode)) { Seq(DeriveTask(lookaheadNode)) } else Seq()
                        val finishTask = {
                            val condition: Condition = lookahead match {
                                case _: LookaheadIs => After(lookaheadNode, nextGen)
                                case _: LookaheadExcept => Until(lookaheadNode, nextGen)
                            }
                            FinishTask(startNode, condition, None)
                        }
                        (cc.updateGraph(_.addNode(lookaheadNode)), finishTask +: newDeriveTask)
                }
            case SequenceNode(symbolId, pointer, _, _) =>
                grammar.nsequences(symbolId) match {
                    case Sequence(_, Seq()) =>
                        assert(pointer == 0)
                        // 빈 sequence node는 즉각 finish task 만들기
                        (cc, Seq(FinishTask(startNode, True, None)))
                    case Sequence(_, sequence) =>
                        assert(pointer < sequence.length)
                        // sequence(pointer)로 symbol node 만들기
                        derive(Set(sequence(pointer)))
                }
        }
    }

    def finishTask(nextGen: Int, task: FinishTask, cc: Context): (Context, Seq[Task]) = {
        val FinishTask(node, condition, lastSymbol) = task

        // nodeSymbolOpt에서 opt를 사용하는 것은 finish는 SequenceNode에 대해서도 실행되기 때문
        val nodeSymbolOpt = grammar.nsymbols get node.symbolId
        val resultCondition = nodeSymbolOpt match {
            case Some(Except(_, body, except)) =>
                val lastSymbolId = lastSymbol.get
                if (lastSymbolId == body) {
                    conjunct(condition, Exclude(SymbolNode(except, node.beginGen)))
                } else {
                    assert(lastSymbolId == except)
                    False
                }
            case _ => condition
        }
        if ((cc.finishes contains (node, resultCondition)) || (resultCondition == False)) {
            (cc, Seq())
        } else {
            val chainCondition = nodeSymbolOpt match {
                case Some(_: Longest) => conjunct(resultCondition, Until(node, nextGen))
                case Some(_: EagerLongest) => conjunct(resultCondition, Alive(node, nextGen))
                case _ => resultCondition
            }
            val incomingEdges = cc.graph.edgesByDest(node)
            val chainTasks: Seq[Task] = incomingEdges.toSeq flatMap {
                case SimpleEdge(incoming: SymbolNode, _) =>
                    Some(FinishTask(incoming, chainCondition, Some(node.symbolId)))
                case SimpleEdge(incoming: SequenceNode, _) =>
                    Some(ProgressTask(incoming, chainCondition))
                case JoinEdge(start, end, join) if end == node =>
                    cc.finishes.of(join).getOrElse(Set()) map { otherSideCondition =>
                        FinishTask(start, conjunct(chainCondition, otherSideCondition), Some(node.symbolId))
                    }
                case JoinEdge(start, end, join) if join == node =>
                    cc.finishes.of(end).getOrElse(Set()) map { otherSideCondition =>
                        FinishTask(start, conjunct(chainCondition, otherSideCondition), Some(node.symbolId))
                    }
            }
            (cc.updateFinishes(_.update(node, resultCondition)), chainTasks)
        }
    }

    def progressTask(nextGen: Int, task: ProgressTask, cc: Context): (Context, Seq[Task]) = {
        val ProgressTask(node @ SequenceNode(symbolId, pointer, beginGen, _), condition) = task

        val Sequence(_, sequence) = grammar.nsequences(symbolId)
        assert(pointer < sequence.length)

        if (pointer + 1 == sequence.length) {
            // append되면 finish할 수 있는 상태
            (cc, Seq(FinishTask(node, condition, None)))
        } else {
            val updatedNode = SequenceNode(symbolId, pointer + 1, beginGen, nextGen)
            if (cc.graph.nodes contains updatedNode) {
                val updatedConditions = cc.progresses.of(node).get map { conjunct(_, condition) }
                if (cc.progresses contains (updatedNode, updatedConditions)) {
                    (cc, Seq())
                } else {
                    (cc.updateProgresses(_.update(updatedNode, updatedConditions))
                        .updateFinishes(_.update(node, updatedConditions)), Seq(DeriveTask(updatedNode)))
                }
            } else {
                val incomingEdges = cc.graph.edgesByDest(node) map { _.asInstanceOf[SimpleEdge] }
                val newGraph = incomingEdges.foldLeft(cc.graph.addNode(updatedNode)) { (graph, edge) =>
                    graph.addEdge(SimpleEdge(edge.start, updatedNode))
                }
                val updatedConditions = cc.progresses.of(node).get map { conjunct(_, condition) }
                (cc.updateGraph(newGraph)
                    .updateProgresses(_.update(updatedNode, updatedConditions))
                    .updateFinishes(_.update(node, updatedConditions)), Seq(DeriveTask(updatedNode)))
            }
        }
    }

    def finishableTermNodes(context: Context, nextGen: Int, input: Inputs.Input): Set[Node] = {
        def acceptable(symbolId: Int): Boolean = grammar.nsymbols(symbolId) match {
            case Terminal(terminal) => terminal accept input
            case _ => false
        }
        context.graph.nodes collect {
            case node @ SymbolNode(symbolId, `nextGen`) if acceptable(symbolId) => node
        }
    }
    def termNodes(context: Context, nextGen: Int): Set[Node] = {
        context.graph.nodes collect {
            case node @ SymbolNode(symbolId, `nextGen`) if grammar.nsymbols(symbolId).isInstanceOf[Terminal] => node
        }
    }
    def trim(context: Context, starts: Set[Node], ends: Set[Node]): Context = {
        // TODO
        context
    }
    def revert(nextGen: Int, context: Context, finishes: Results[Node], survived: Set[Node]): Context = {
        context.updateFinishes(_.mapCondition(_.evaluate(nextGen, finishes, survived))).updateProgresses(_.mapCondition(_.evaluate(nextGen, finishes, survived))).trimProgresses
    }
}
