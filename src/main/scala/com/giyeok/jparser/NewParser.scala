package com.giyeok.jparser

import com.giyeok.jparser.Kernels._
import com.giyeok.jparser.Symbols._
import com.giyeok.jparser.ParseTree._
import com.giyeok.jparser.Inputs.ConcreteInput
import com.giyeok.jparser.ParsingErrors._
import DerivationGraph._
import com.giyeok.jparser.Inputs.Input

class NewParser(val grammar: Grammar) {
    import NewParser._

    private val derivationGraphCache = scala.collection.mutable.Map[NontermKernel[_], DerivationGraph]()
    def derive(kernel: NontermKernel[Nonterm]): DerivationGraph = {
        derivationGraphCache get kernel match {
            case Some(graph) => graph
            case None =>
                val graph = DerivationGraph.deriveFromKernel(grammar, kernel)
                derivationGraphCache(kernel) = graph
                graph
        }
    }

    case class Graph(nodes: Set[Node], edges: Set[Edge]) {
        def termNodes: Set[TermNode] = nodes collect { case n: TermNode => n }

        def expand(gen: Int, baseDerivable: Derivable, derivationGraph: DerivationGraph): (Graph, Set[TermNode]) = {
            val Derivable(baseNode: NontermNode[Nonterm], givenRevertTriggers: Set[Trigger]) = baseDerivable
            assert(derivationGraph.baseNode.kernel == baseNode.kernel)
            def newNode(dnode: NewNode with NonEmptyNode): Node = dnode match {
                case NewTermNode(kernel) => TermNode(kernel)
                case NewAtomicNode(kernel, liftBlockTrigger, reservedReverter) =>
                    AtomicNode(kernel, gen, liftBlockTrigger map { t => newNode(t.asInstanceOf[NewNode with NonEmptyNode]) }, reservedReverter map { Trigger.Type.of _ })
                case NewNonAtomicNode(kernel, progress) =>
                    NonAtomicNode(kernel, gen, progress)
            }
            val nodesMap: Map[DerivationGraph.Node, Node] =
                (((derivationGraph.nodes - derivationGraph.baseNode) map { n =>
                    (n -> newNode(n.asInstanceOf[NewNode with NonEmptyNode]))
                }).toMap) + (derivationGraph.baseNode -> baseNode)
            val newEdges = derivationGraph.edges map {
                case DerivationGraph.SimpleEdge(start, end, edgeRevertTriggers) =>
                    val derivedRevertTriggers = edgeRevertTriggers map {
                        case DerivationGraph.Trigger(node, triggerType) =>
                            Trigger(nodesMap(node), Trigger.Type.of(triggerType))
                    }
                    val startNode = nodesMap(start).asInstanceOf[NontermNode[Nonterm]]
                    // baseNode에서 나오는 엣지에는 revert triggers가 붙어야 한다
                    val revertTriggers = if (baseNode == startNode) (derivedRevertTriggers ++ givenRevertTriggers) else derivedRevertTriggers
                    SimpleEdge(startNode, nodesMap(end), revertTriggers)
                case DerivationGraph.JoinEdge(start, end, join) =>
                    assert(start.kernel.isInstanceOf[JoinKernel])
                    val startNode = nodesMap(start).asInstanceOf[AtomicNode[Join]]
                    assert(startNode != baseNode || givenRevertTriggers.isEmpty)
                    JoinEdge(startNode, nodesMap(end), nodesMap(join))
            }
            (Graph(nodes ++ nodesMap.values.toSet, edges ++ newEdges), nodesMap.values.toSet[Node] collect { case n: TermNode => n })
        }

        def lift(gen: Int, termLifts: Set[(TermNode, Input)], liftBlockedNodes: Set[AtomicNode[_]]): (Graph, Set[Derivable], Set[Lift]) = {
            sealed trait LiftTask
            case class TermLift(before: TermNode, by: Input) extends LiftTask
            case class NontermLift(before: NontermNode[Nonterm], by: ParseNode[Symbol], revertTriggers: Set[Trigger]) extends LiftTask
            case class JoinLift(before: NontermNode[Join], by: ParseNode[Symbol], join: ParseNode[Symbol], revertTriggers: Set[Trigger]) extends LiftTask
            def lift(queue: List[LiftTask], graph: Graph, derivables: Set[Derivable], lifts: Set[Lift]): (Graph, Set[Derivable], Set[Lift]) = {
                def chainLift(node: Node, parsed: ParseNode[Symbol], revertTriggers: Set[Trigger]): Set[LiftTask] = {
                    val incomingEdges = graph.incomingEdgesTo(node)
                    incomingEdges flatMap {
                        case SimpleEdge(start, end, edgeRevertTriggers) =>
                            assert(node == end)
                            Set[LiftTask](NontermLift(start, parsed, revertTriggers ++ edgeRevertTriggers))
                        case JoinEdge(start, end, join) =>
                            if (end == node) {
                                (lifts filter { _.before == join } map { l =>
                                    JoinLift(start, parsed, l.parsed, revertTriggers)
                                }).toSet[LiftTask]
                            } else {
                                assert(join == node)
                                (lifts filter { _.before == end } map { l =>
                                    JoinLift(start, l.parsed, parsed, revertTriggers)
                                }).toSet[LiftTask]
                            }
                    }
                }
                queue match {
                    case TermLift(before, by) +: rest =>
                        val (afterKernel, parsed) = (before.kernel.lifted, ParsedTerminal(before.kernel.symbol, by))
                        // TermNode는 atomic node이므로 한 번 lift하면 finishable && !derivable 해진다
                        assert(afterKernel.finishable && !afterKernel.derivable)
                        // lifts에 Lift 추가하고
                        val newLift = Lift(before, afterKernel, parsed, None, Set())
                        // TermNode는 이번 세대에서 항상 사라지기 때문에 (항상 dangled이므로) graph에서 지우고
                        // before로 incoming node들에 대해 새로운 LiftTask를 추가
                        val newLiftTasks = chainLift(before, parsed, Set())
                        lift(newLiftTasks.toList ++: rest, graph, derivables, lifts + newLift)

                    case NontermLift(before @ AtomicNode(kernel, _, _, reservedReverter), by, revertTriggers) +: rest =>
                        val (afterKernel, parsed) = (kernel.lifted, ParsedSymbol(kernel.symbol, by))
                        // Atomic node는 한 번 lift하면 finishable && !derivable 해진다
                        assert(afterKernel.finishable && !afterKernel.derivable)
                        // lifts에 Lift 추가하고
                        val newRevertTriggers = revertTriggers ++ (reservedReverter map { Trigger(before, _) })
                        val newLift = Lift(before, afterKernel, parsed, None, newRevertTriggers)
                        // dangled == true이면 before를 graph에서 지우고
                        // before로 incoming node들에 대해 새로운 LiftTask를 추가
                        val newLiftTasks = chainLift(before, parsed, newRevertTriggers)
                        lift(newLiftTasks.toList ++: rest, graph, derivables, lifts + newLift)

                    case NontermLift(before @ NonAtomicNode(kernel, _, progress), by, revertTriggers) +: rest =>
                        val (afterKernel: NonAtomicNontermKernel[_], parsed: ParsedSymbolsSeq[_]) = kernel.lifted(progress, by)
                        val incomingEdges0 = graph.incomingEdgesTo(before)
                        assert(incomingEdges0 forall { _.isInstanceOf[SimpleEdge] })
                        val incomingEdges = incomingEdges0 map { _.asInstanceOf[SimpleEdge] }

                        var newDerivables = derivables
                        var afterNode: Option[Node] = None
                        // afterKernel.derivable하면 새로 생긴 노드는 roottip이 되고 그 이후로는 root 노드가 되므로 살려야 한다
                        var newGraph = graph

                        if (afterKernel.derivable) {
                            // afterKernel과 newProgress로 새로운 node 만들고 start -> 새 노드로 가는 엣지 추가하고, 이 때 before에 붙어있던 edge revert trigger들은 이 엣지에도 붙여준다
                            val newNode = NonAtomicNode(afterKernel, gen, parsed)
                            val newEdges: Set[Edge] = incomingEdges map {
                                case SimpleEdge(start, _, edgeRevertTriggers) =>
                                    SimpleEdge(start, newNode, edgeRevertTriggers ++ revertTriggers)
                            }
                            // 새로 생성된 노드는 아래에서 만드는 Lift에 after node로 지정해주고 derviables에 추가한다
                            newDerivables += Derivable(newNode, revertTriggers)
                            newGraph = newGraph.withNodeAndEdges(newNode, newEdges)
                            afterNode = Some(newNode)
                            // TODO DerivationGraph of 새로 생성된 노드에 baseNodeLift가 있는 경우(nullable 처리)
                            // - parsed와 lift의 parsed를 merge해서 위에서 newEdges 만든것처럼 새 노드를 만들고
                            // - 위에서 newEdges 만든 것처럼 새 노드로 엣지 만들어주고
                            // - 새로운 lift task를 만들어서 추가해준다
                            derive(afterKernel).baseNodeLifts
                        }

                        val newLift = Lift(before, afterKernel, parsed, afterNode, revertTriggers)
                        val newLiftTasks = if (afterKernel.finishable) chainLift(before, parsed, revertTriggers).toList else List()
                        lift(newLiftTasks ++: rest, newGraph, newDerivables, lifts + newLift)

                    case List() => (graph, derivables, lifts)
                }
                // dangled이면 graph에서 lift.before 노드와 incomingEdges를 모두 제거한다
            }
            val (graph, derivables, lifts) = lift(termLifts.toList map { p => TermLift(p._1, p._2) }, this, Set(), Set())
            // TODO graph에서 derivables로 reachable한 노드/엣지만 추린다(기존의 rootTip/root와 같은 의미)
            (graph, derivables, lifts)
        }

        // lifts에 의해 trigger되는 node/edge를 제거한 그래프와 temporarily lift block되는 노드들의 집합을 반환
        def revert(liftedGraph: Graph, lifts: Set[Lift]): (Graph, Set[AtomicNode[_]]) = {
            val liftedNodes = lifts map { _.before }
            assert(liftedNodes subsetOf nodes)

            val tempLiftBlockNodes: Set[AtomicNode[_]] = nodes collect {
                case node @ AtomicNode(_, _, Some(liftBlockTrigger), _) if liftedNodes contains liftBlockTrigger => node
            }
            val survivedEdges = edges filterNot {
                case SimpleEdge(_, _, revertTriggers) =>
                    revertTriggers exists {
                        case Trigger(node, Trigger.Type.Lift) => liftedNodes contains node
                        case Trigger(node, Trigger.Type.Alive) => liftedGraph.nodes contains node
                    }
                case _ => false
            }
            (Graph(nodes, survivedEdges), tempLiftBlockNodes)
        }

        // baseNode에서 도달 가능한 node와 edge로만 구성된 subgraph를 반환
        def reachableFrom(baseNode: Node): Graph = {
            def traverse(queue: List[Node], cc: Graph): Graph =
                queue match {
                    case node +: rest =>
                        val outgoingEdges = outgoingEdgesFrom(node)
                        val newNodes = outgoingEdges flatMap {
                            case SimpleEdge(_, end, revertTriggers) =>
                                Set(end) ++ (revertTriggers map { _.node })
                            case JoinEdge(_, end, join) =>
                                Set(end, join)
                        }
                        traverse(rest ++ (newNodes -- cc.nodes).toList, Graph(cc.nodes ++ newNodes, cc.edges ++ outgoingEdges))
                    case List() => cc
                }
            traverse(List(baseNode), Graph(Set(baseNode), Set()))
        }

        def incomingEdgesTo(node: Node): Set[Edge] = edges collect {
            case e @ SimpleEdge(_, `node`, _) => e
            case e @ JoinEdge(_, `node`, _) => e
            case e @ JoinEdge(_, _, `node`) => e
        }
        def outgoingEdgesFrom(node: Node): Set[Edge] = edges filter {
            case SimpleEdge(start, _, _) => start == node
            case JoinEdge(start, _, _) => start == node
        }
        def withNodeAndEdges(node: Node, edges: Set[Edge]): Graph = Graph(this.nodes + node, this.edges ++ edges)
    }

    case class ProceedDetail(
            expandedGraph: Graph,
            eligibleTermNodes0: Set[TermNode],
            liftedGraph0: Graph,
            lifts0: Set[Lift],
            revertedGraph: Graph,
            tempLiftBlockNodes: Set[AtomicNode[_]],
            trimmedRevertedGraph: Graph,
            eligibleTermNodes: Set[TermNode],
            liftedGraph: Graph,
            nextDerivables: Set[Derivable],
            lifts: Set[Lift],
            nextContext: ParsingContext) {
        def expandStage = expandedGraph
        def preLiftStage = (eligibleTermNodes0, liftedGraph0, lifts0)
        def revertStage = (revertedGraph, tempLiftBlockNodes, trimmedRevertedGraph)
        def finalLiftStage = (eligibleTermNodes, liftedGraph, nextDerivables, lifts)
    }

    case class ParsingContext(gen: Int, startNode: NontermNode[Start.type], graph: Graph, derivables: Set[Derivable], results: Set[ParseNode[Symbol]]) {
        assert((derivables map { _.node }).asInstanceOf[Set[Node]] subsetOf graph.nodes)
        def proceedDetail(input: ConcreteInput): Either[ProceedDetail, ParsingError] = {
            val (expandedGraph, eligibleTermNodes0) = derivables.foldLeft((graph, Set[TermNode]())) { (cc, derivable) =>
                derive(derivable.node.kernel).subgraphTo(input) match {
                    case Some(graph) =>
                        // expand `graph` from `derivable` node
                        // and lift from terminal nodes with `input`
                        val d = cc._1.expand(gen, derivable, graph)
                        (d._1, cc._2 ++ d._2)
                    case None =>
                        // cc._1(그래프)에서 derivable 로 reachable하고 다른 derivable로는 reachable하지 않은 노드/엣지를 지웠으면 좋겠는데..
                        // alive trigger가 제대로 동작하려면 이 기능이 필요
                        cc
                }
            }
            if (eligibleTermNodes0.isEmpty) {
                Right(UnexpectedInput(input))
            } else {
                assert(eligibleTermNodes0 forall { _.kernel.symbol.accept(input) })
                def liftsFromTermNodes(termNodes: Set[TermNode]): Set[(TermNode, Input)] = termNodes map { (_, input) }

                // 1. reverter 무시하고 우선 한번 lift를 진행한다
                val (liftedGraph0, _, lifts0) = expandedGraph.lift(gen, liftsFromTermNodes(eligibleTermNodes0), Set())
                // 2. lift가 진행된 뒤 trigger되는 reverter를 적용한 그래프를 만든다
                val (revertedGraph, tempLiftBlockNodes) = expandedGraph.revert(liftedGraph0, lifts0)
                // 3. (optional?) baseNode에서 reachable한 node와 edge로만 구성된 그래프를 추린다
                val trimmedRevertedGraph = revertedGraph.reachableFrom(startNode)
                // 4. 추려진 그래프에 대해 다시 lift를 진행한다 (사실은 liftedGraph0, lifts0 등을 고쳐서 쓸 수도 있겠지만 귀찮으니..)
                val eligibleTermNodes = eligibleTermNodes0 intersect trimmedRevertedGraph.termNodes
                // 5. 그런 뒤에 eligibleTermNodes1이 비어있으면 에러를 반환한다
                if (eligibleTermNodes.isEmpty) {
                    Right(UnexpectedInput(input))
                } else {
                    val (liftedGraph, nextDerivables, lifts) = trimmedRevertedGraph.lift(gen, liftsFromTermNodes(eligibleTermNodes), tempLiftBlockNodes)
                    // 6. reverter가 적용되어 계산된 ParsingContext를 반환한다
                    val nextContext = new ParsingContext(gen + 1, startNode, liftedGraph, nextDerivables, lifts collect { case Lift(`startNode`, _, parsed, _, _) => parsed })
                    Left(new ProceedDetail(expandedGraph, eligibleTermNodes0, liftedGraph0, lifts0, revertedGraph, tempLiftBlockNodes, trimmedRevertedGraph, eligibleTermNodes, liftedGraph, nextDerivables, lifts, nextContext))
                }
            }
        }
        def proceed(input: ConcreteInput): Either[ParsingContext, ParsingError] = proceedDetail(input) match {
            case Left(detail) => Left(detail.nextContext)
            case Right(error) => Right(error)
        }
    }

    val initialContext = {
        val startKernel = StartKernel(0)
        val startNode = AtomicNode(startKernel, 0, None, None)
        new ParsingContext(0, startNode, Graph(Set(startNode), Set()), Set(Derivable(startNode, Set())), derive(startKernel).baseNodeLifts map { _.parsed })
    }

    def parse(source: Inputs.ConcreteSource): Either[ParsingContext, ParsingError] =
        source.foldLeft[Either[ParsingContext, ParsingError]](Left(initialContext)) {
            (ctx, input) =>
                ctx match {
                    case Left(ctx) => ctx proceed input
                    case error @ Right(_) => error
                }
        }
    def parse(source: String): Either[ParsingContext, ParsingError] =
        parse(Inputs.fromString(source))
}

object NewParser {
    sealed trait Node {
        val kernel: Kernel
    }
    sealed trait NontermNode[+T <: Nonterm] extends Node {
        val kernel: NontermKernel[T]
        val gen: Int
    }
    case class TermNode(kernel: TerminalKernel) extends Node
    case class AtomicNode[+T <: AtomicSymbol with Nonterm](kernel: AtomicNontermKernel[T], gen: Int, liftBlockTrigger: Option[Node], reservedReverter: Option[Trigger.Type.Value]) extends NontermNode[T]
    case class NonAtomicNode[T <: NonAtomicSymbol with Nonterm](kernel: NonAtomicNontermKernel[T], gen: Int, progress: ParsedSymbolsSeq[T]) extends NontermNode[T]

    case class Derivable(node: NontermNode[Nonterm], reverters: Set[Trigger])

    case class Trigger(node: Node, triggerType: Trigger.Type.Value)
    object Trigger {
        object Type extends Enumeration {
            val Lift, Alive = Value
            def of(t: DerivationGraph.Trigger.Type.Value) = t match {
                case DerivationGraph.Trigger.Type.Lift => Lift
                case DerivationGraph.Trigger.Type.Alive => Alive
            }
        }
    }

    sealed trait Edge
    case class SimpleEdge(start: NontermNode[Nonterm], end: Node, revertTriggers: Set[Trigger]) extends Edge
    case class JoinEdge(start: AtomicNode[Join], end: Node, join: Node) extends Edge

    case class Lift(before: Node, afterKernel: Kernel, parsed: ParseNode[Symbol], after: Option[Node], revertTriggers: Set[Trigger])
}
