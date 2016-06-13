package com.giyeok.jparser

import com.giyeok.jparser.Symbols._
import com.giyeok.jparser.Inputs.ConcreteInput
import com.giyeok.jparser.ParsingErrors._
import com.giyeok.jparser.Inputs.Input
import ParsingGraph._
import com.giyeok.jparser.Inputs.TermGroupDesc
import com.giyeok.jparser.DGraph.BaseNode

case class CtxGraph[R <: ParseResult](
        nodes: Set[Node],
        edges: Set[Edge],
        results: Results[Node, R],
        progresses: Results[SequenceNode, R]) extends ParsingGraph[R] {
    def create(nodes: Set[Node], edges: Set[Edge], results: Results[Node, R], progresses: Results[SequenceNode, R]): CtxGraph[R] = {
        CtxGraph(nodes, edges, results, progresses)
    }
}

class NewParser[R <: ParseResult](val grammar: Grammar, val resultFunc: ParseResultFunc[R], derivationFunc: DerivationSliceFunc[R]) extends LiftTasks[R, CtxGraph[R]] {
    type Graph = CtxGraph[R]
    val startNode = AtomicNode(Start, 0)

    trait CtxGraphTransition {
        val title: String
        val baseGraph: Graph
        val nextGraph: Graph
    }
    trait ParsingTaskTransition {
        val baseGraph: Graph
        val initialTasks: Seq[ParsingTasks[R, Graph]#Task]
        // TODO
    }
    case class ExpandTransition(title: String, baseGraph: Graph, nextGraph: Graph, initialTasks: Set[NewParser[R]#Task]) extends CtxGraphTransition
    case class LiftTransition(title: String, baseGraph: Graph, nextGraph: Graph, initialTasks: Set[NewParser[R]#Task], nextDerivables: Set[Node]) extends CtxGraphTransition
    case class TrimmingTransition(title: String, baseGraph: Graph, nextGraph: Graph, startNode: Node, endNodes: Set[Node]) extends CtxGraphTransition
    case class RevertTransition(title: String, baseGraph: Graph, nextGraph: Graph, firstLiftResult: Graph, revertBaseResults: Results[Node, R]) extends CtxGraphTransition

    case class ParsingCtxTransition(
        firstStage: Option[(ExpandTransition, LiftTransition)],
        secondStage: Option[(TrimmingTransition, RevertTransition)],
        finalTrimming: Option[TrimmingTransition])

    object ParsingCtx {
        // graph를 expand한다
        // - baseAndGraphs에 있는 튜플의 _1를 base로 해서 _2의 내용을 shiftGen해서 그래프에 추가한다
        // - expandAll은 base node의 dgraph 전체를 추가하고
        // - expand는 base node의 dgraph중 input이 prelift된 것을 추가한다
        def expandAll(graph: Graph, gen: Int, nextGen: Int, derivables: Set[NontermNode], input: ConcreteInput): (Graph, Set[Task]) = {
            val baseAndGraphs: Set[(NontermNode, DGraph[R])] = derivables map { node => node -> derivationFunc.derive(node) }

            // baseAndGraphs의 베이스 노드가 모두 graph에 포함되어 있어야 한다
            assert(baseAndGraphs map { _._1.asInstanceOf[Node] } subsetOf graph.nodes)
            // expand 하기 전에는 이전 세대의 TermNode가 그래프에 있으면 안 됨
            assert({
                val invalidTermNodes = (graph.nodes collect { case node @ TermNode(sym, nodeGen) if nodeGen < gen => node })
                if (!invalidTermNodes.isEmpty) {
                    println(invalidTermNodes)
                }
                invalidTermNodes.isEmpty
            })

            // graph에 DerivationGraph.shiftGen해서 추가해준다
            // - edgesFromBaseNode/edgesNotFromBaseNode 구분해서 잘 처리
            val (newGraph, termNodes) = baseAndGraphs.foldLeft((graph, Set[TermNode]())) { (result, pair) =>
                val (baseNode, dgraph) = pair

                val newNodes = (dgraph.nodes map { node =>
                    val newNode = node match {
                        case _: BaseNode => baseNode
                        case n => n.shiftGen(gen)
                    }
                    node -> newNode
                }).toMap
                val newEdges: Set[Edge] = dgraph.edges map {
                    case SimpleEdge(start, end, condition) =>
                        SimpleEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end),
                            condition.shiftGen(gen))
                    case ReferEdge(start, end) =>
                        ReferEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end))
                    case JoinEdge(start, end, join) =>
                        JoinEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end),
                            newNodes(join))
                }
                // dgraph.results.of(dgraph.baseNode)와 dgraph.progresses.of(dgraph.baseNode)는 항상 비어 있으므로 여기서 신경쓰지 않아도 됨
                // - 이 내용들은 dgraph.baseResults와 dgraph.baseProgresses로 들어는데 이 부분은 이전 세대의 lift에서 이미 처리되었을 것임

                val newProgresses = dgraph.progresses map (_.shiftGen(gen), _.shiftGen(gen), result => result)

                val newNodesSet = newNodes.values.toSet[Node]
                val updatedGraph = result._1.withNodesEdgesProgresses(newNodesSet, newEdges, newProgresses).asInstanceOf[Graph]
                val updatedTermNodes = result._2 ++ (newNodesSet collect { case n: TermNode => n })
                (updatedGraph, updatedTermNodes)
            }
            val termFinishingTasks: Set[Task] = termNodes collect {
                case termNode: TermNode if termNode.symbol accept input => FinishingTask(nextGen, termNode, TermResult(resultFunc.terminal(input)), Condition.True)
            }
            (newGraph, termFinishingTasks)
        }

        // - expand는 base node의 dgraph중 input이 prelift된 것을 추가한다
        //   - expand에서는 result도 잘 추가해야 함
        //   - sliced dgraph의 
        //     - baseProgresses로 SequenceProgressTask를, 
        //     - baseResult로 FinishingTask(DeriveTask인가?)를 추가하고
        //     - derivables로 DeriveTask(맞나?)를 추가한다
        def expand(graph: Graph, gen: Int, nextGen: Int, derivables: Set[NontermNode], input: ConcreteInput): (Graph, Set[Task], Results[Node, R]) = {
            val baseAndGraphs: Set[(NontermNode, (DGraph[R], Set[NontermNode]))] = derivables flatMap { node =>
                derivationFunc.deriveSlice(node) find { _._1 contains input } map { kv => (node -> kv._2) }
            }
            val (newGraph, tasks, results) = baseAndGraphs.foldLeft((graph, Set[Task](), Results[Node, R]())) { (cc, pair) =>
                val (graphCC, tasksCC, resultsCC) = cc
                val (baseNode, (dgraph, newDerivables)) = pair
                assert(graph.nodes contains baseNode)

                // 그래프에 baseNode로부터 dgraph를 추가
                // - 이 때 dgraph.progresses의 result에 substTermFunc를 해줘야 함
                val newNodes = (dgraph.nodes map { node =>
                    val newNode = node match {
                        case _: BaseNode => baseNode
                        case n => n.shiftGen(gen)
                    }
                    node -> newNode
                }).toMap
                val newEdges: Set[Edge] = dgraph.edges map {
                    case SimpleEdge(start, end, condition) =>
                        SimpleEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end),
                            condition.shiftGen(gen))
                    case ReferEdge(start, end) =>
                        ReferEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end))
                    case JoinEdge(start, end, join) =>
                        JoinEdge(
                            newNodes(start).asInstanceOf[NontermNode],
                            newNodes(end),
                            newNodes(join))
                }
                val newProgresses = dgraph.progresses.map(_.shiftGen(gen), _.shiftGen(gen), resultFunc.substTermFunc(_, input))
                val newNodesSet = newNodes.values.toSet[Node]
                val expandedGraph = graphCC.withNodesEdgesProgresses(newNodesSet, newEdges, newProgresses).asInstanceOf[Graph]

                // 이제 확장된 부분에서 필요한 task를 만들어줌
                // - newDerivables 각각에 대해 DeriveTask 만들어주고
                val deriveTasks = newDerivables map { derivableNode => DeriveTask(nextGen, derivableNode.shiftGen(gen).asInstanceOf[NontermNode]) }
                // - dgraph.baseResults 각각에 대해 FinishingTask 만들어주고
                val finishingTasks = dgraph.baseResults map { kv =>
                    val (condition, resultAndSymbol) = kv
                    FinishingTask(nextGen, baseNode, resultAndSymbol mapResult { resultFunc.substTermFunc(_, input) }, condition.shiftGen(gen))
                }
                // - dgraph.baseProgresses 각각에 대해 SequenceProgressTask 만들어주고
                val progressTasks = dgraph.baseProgresses map { kv =>
                    val (condition, childAndSymbol) = kv
                    SequenceProgressTask(nextGen, baseNode.asInstanceOf[SequenceNode], childAndSymbol mapResult { resultFunc.substTermFunc(_, input) }, condition.shiftGen(gen))
                }
                val newTasks: Set[Task] = (deriveTasks ++ finishingTasks ++ progressTasks)

                // dgraph.results는 revertTrigger 처리할 때 필요하므로 substTermFunc/merge해서 전달해주고
                val preliftResults = dgraph.results.map(_.shiftGen(gen), _.shiftGen(gen), resultFunc.substTermFunc(_, input))

                (expandedGraph, tasksCC ++ newTasks, resultsCC.merge(preliftResults, resultFunc))
            }
            (newGraph, tasks, results)
        }

        // 이 그래프에서 lift한 그래프와 그 그래프의 derivation tip nodes를 반환한다
        def lift(graph: Graph, nextGen: Int, initialTasks: Set[Task], liftBlockedNodes: Map[AtomicNode, Condition]): (Graph, Set[SequenceNode]) = {
            // FinishingTask.node가 liftBlockedNodes에 있으면 해당 task는 제외
            // DeriveTask(node)가 나오면 실제 Derive 진행하지 않고 derivation tip nodes로 넣는다 (이 때 node는 항상 SequenceNode임)
            def rec(tasks: List[Task], graphCC: Graph, derivablesCC: Set[SequenceNode]): (Graph, Set[SequenceNode]) =
                tasks match {
                    case task +: rest =>
                        task match {
                            case DeriveTask(_, node: SequenceNode) =>
                                val immediateProgresses: Seq[SequenceProgressTask] = (derivationFunc.derive(node).baseProgresses map { kv =>
                                    val (condition, childAndSymbol) = kv

                                    // triggers를 nextGen만큼 shift해주기
                                    val shiftedCondition: Condition = condition.shiftGen(nextGen)

                                    SequenceProgressTask(nextGen, node.asInstanceOf[SequenceNode], childAndSymbol, shiftedCondition)
                                }).toSeq

                                rec(rest ++ immediateProgresses, graphCC, derivablesCC + node.asInstanceOf[SequenceNode])
                            case DeriveTask(_, _) =>
                                // 이런 상황은 발생할 수 없음
                                throw new AssertionError("")
                            case task: FinishingTask =>
                                val liftBlocking = task.node match {
                                    case node: AtomicNode => liftBlockedNodes get node
                                    case _ => None
                                }
                                liftBlocking match {
                                    case None =>
                                        // liftedBlockedNodes에 있지 않으면 일반 진행
                                        val (newGraphCC, newTasks) = finishingTask(task, graphCC)
                                        rec(rest ++ newTasks, newGraphCC, derivablesCC)
                                    case Some(condition) if condition.permanentFalse =>
                                        // liftBlocking 조건이 permanentFalse이면 이 리프트는 무시하고 진행한다
                                        rec(rest, graphCC, derivablesCC)
                                    case Some(condition) =>
                                        // 여기서 condition이 만족되는 동안에는 이 lift가 실패한 것으로 동작해야 한다
                                        // TODO permanentFalse가 아닌 조건이 올라왔으면 condition을 task.condition에 합쳐서 진행한다 - 우선은 conjunct로 합쳐지게 되어 있는데.. 맞는듯?
                                        val (newGraphCC, newTasks) = finishingTask(FinishingTask(task.nextGen, task.node, task.resultWithType, Condition.conjunct(task.condition, condition)), graphCC)
                                        rec(rest ++ newTasks, newGraphCC, derivablesCC)
                                }
                            case task: SequenceProgressTask =>
                                val (newGraphCC, newTasks) = sequenceProgressTask(task, graphCC)
                                rec(rest ++ newTasks, newGraphCC, derivablesCC)
                        }
                    case List() => (graphCC, derivablesCC)
                }
            rec(initialTasks.toList, graph.withNoResults.asInstanceOf[Graph], Set())
        }

        // graph.results를 이용해서 revertTrigger 조건을 비교해서 지워야 할 edge/results를 제거한 그래프를 반환한다
        def revert(gen: Int, graph: Graph, preliftResults: Results[Node, R], preliftNodes: Set[Node]): Graph = {
            val nextProgresses = graph.progresses.entries.foldLeft(Results[SequenceNode, R]()) { (cc, entry) =>
                val (node, condition, result) = entry
                val evaluatedCondition = condition.evaluate(gen, preliftResults, preliftNodes)
                if (evaluatedCondition.permanentFalse) cc else {
                    cc.of(node, evaluatedCondition) match {
                        case Some(existingResult) =>
                            cc.update(node, evaluatedCondition, resultFunc.merge(existingResult, result))
                        case None =>
                            cc.update(node, evaluatedCondition, result)
                    }
                }
            }

            val nextResults = graph.results.entries.foldLeft(Results[Node, R]()) { (cc, entry) =>
                val (node, condition, result) = entry
                val evaluatedCondition = condition.evaluate(gen, preliftResults, preliftNodes)
                if (evaluatedCondition.permanentFalse) cc else {
                    cc.of(node, evaluatedCondition) match {
                        case Some(existingResult) =>
                            cc.update(node, evaluatedCondition, resultFunc.merge(existingResult, result))
                        case None =>
                            cc.update(node, evaluatedCondition, result)
                    }
                }
            }

            // graph.nodes 중 progress가 없는 sequence node는 없애주어야 함
            val validNodes = graph.nodes filter {
                case node: SequenceNode => nextProgresses.of(node).isDefined
                case _ => true
            }

            // validNodes에 속하지 않는 노드와 연관이 있는 엣지를 제거한다
            val nextEdges: Set[Edge] = graph.edges flatMap {
                case SimpleEdge(start, end, condition) =>
                    if ((validNodes contains start) && (validNodes contains end)) {
                        val evaluatedCondition = condition.evaluate(gen, preliftResults, preliftNodes)
                        if (evaluatedCondition.permanentFalse) None else Some(SimpleEdge(start, end, evaluatedCondition))
                    } else None
                case edge @ ReferEdge(start, end) =>
                    if ((validNodes contains start) && (validNodes contains end)) Some(edge) else None
                case edge @ JoinEdge(start, end, join) =>
                    if ((validNodes contains start) && (validNodes contains end) && (validNodes contains join)) Some(edge) else None
            }

            // result는 이후에 lift할 때 없애고 시작하기 때문에 의미 없어서 따로 필터링할 필요 없음
            graph.create(graph.nodes, nextEdges, nextResults, nextProgresses)
        }
    }

    case class ParsingCtx(gen: Int, graph: Graph, derivables: Set[NontermNode]) {
        // graph.nodes는 모두 gen이 <= 현재 gen 을 만족해야 한다
        // results나 progresses의 condition에 속한 node 중에는 beginGen = gen + 1 세대인 노드가 있을 수도 있다
        assert(graph.nodes forall {
            case node: TermNode => node.beginGen <= gen
            case node: AtomicNode => node.beginGen <= gen
            case node: SequenceNode => node.beginGen <= gen
        })

        // assert(graph.progresses.keyNodesSet.asInstanceOf[Set[Node]] subsetOf graph.nodes)

        // startNode에서 derivables에 도달 가능한 경로 내의 노드들만 포함되어야 하는데..?
        // assert(graph.subgraphIn(startNode, derivables.asInstanceOf[Set[Node]], resultFunc) == Some(graph))

        def result: Option[R] = {
            graph.results.of(startNode) flatMap { results =>
                resultFunc.merge(
                    results collect {
                        case (condition, result) if condition.eligible => result
                    })
            }
        }

        def proceedDetail(input: ConcreteInput): (ParsingCtxTransition, Either[ParsingCtx, ParsingError]) = {
            val nextGen = gen + 1

            // 1. expand
            // val ((expandedGraph, initialTasks: Set[Task]), preliftResults) = (ParsingCtx.expandAll(graph, gen, nextGen, derivables, input), Results[Node, R]())
            val (expandedGraph, initialTasks: Set[Task], preliftResults) = ParsingCtx.expand(graph, gen, nextGen, derivables, input)

            if (initialTasks.isEmpty) {
                (ParsingCtxTransition(None, None, None), Right(UnexpectedInput(input)))
            } else {
                // Pended terminal nodes
                // Pended node는 이전 세대에 미리 만들어진 현재 세대의 노드
                // revert trigger 등에서 다음 세대의 노드가 미리 만들어질 수 있는데 이런 경우 발생한다
                // 근데 expand될 때 안생겼으면 필요 없어서 안 생긴거 아닌가?
                // 우선 지금은 slice안된 상태로 dgraph의 모든 노드를 expand하니 확실히 필요 없긴 한데.. slice할 때도 필요 없는거 맞나?
                // val pendedTermNodes = graph.nodes collect { case node: TermNode if node.symbol accept input => node ensuring (node.beginGen == gen) }
                val firstExpandTransition = ExpandTransition(s"Gen $gen > (1) First Expansion", graph, expandedGraph, initialTasks.asInstanceOf[Set[NewParser[R]#Task]])

                // 2. 1차 lift
                val (liftedGraph0pre, nextDerivables0) = ParsingCtx.lift(expandedGraph, nextGen, initialTasks, Map())

                val firstLiftTransition = LiftTransition(s"Gen $gen > (2) First Lift", expandedGraph, liftedGraph0pre, initialTasks.asInstanceOf[Set[NewParser[R]#Task]], nextDerivables0.asInstanceOf[Set[Node]])

                // revert할 때는 (expand할 때 발생한 results + liftedGraph0.results)를 사용해야 함
                val revertBaseResults = liftedGraph0pre.results.merge(preliftResults, resultFunc)

                // 3. 1차 트리밍
                (liftedGraph0pre.subgraphIn(startNode, nextDerivables0.asInstanceOf[Set[Node]], resultFunc)) match {
                    case Some(liftedGraph0: Graph) =>
                        val firstLiftTrimmingTransition = TrimmingTransition(s"Gen $gen > (3) First Trimming", liftedGraph0pre, liftedGraph0, startNode, nextDerivables0.asInstanceOf[Set[Node]])

                        assert(liftedGraph0.results == liftedGraph0pre.results)

                        // 4. revert
                        // expandedGraph0에서 liftedGraph0의 results를 보고 조건이 만족된 엣지들/result들 제거 - unreachable 노드들은 밑에 liftedGraphPre->liftedGraph 에서 처리되므로 여기서는 무시해도 됨
                        val revertedGraph: Graph = ParsingCtx.revert(gen, liftedGraph0, revertBaseResults, liftedGraph0.nodes)
                        val revertTransition = RevertTransition(s"Gen $gen > (4) Revert", liftedGraph0, revertedGraph, liftedGraph0, revertBaseResults)

                        // 5. 2차 트리밍
                        val finalGraphOpt = revertedGraph.subgraphIn(startNode, nextDerivables0.asInstanceOf[Set[Node]], resultFunc) map { _.asInstanceOf[Graph] }
                        finalGraphOpt match {
                            case Some(finalGraph) =>
                                val secondLiftTrimmingTransition = TrimmingTransition(s"Gen $gen > (5) Second Trimming", revertedGraph, finalGraph, startNode, nextDerivables0.asInstanceOf[Set[Node]])
                                val nextContext: ParsingCtx = ParsingCtx(nextGen, finalGraph, (nextDerivables0.asInstanceOf[Set[Node]] intersect finalGraph.nodes).asInstanceOf[Set[NontermNode]])
                                val transition = ParsingCtxTransition(
                                    Some(firstExpandTransition, firstLiftTransition),
                                    Some(firstLiftTrimmingTransition, revertTransition),
                                    Some(secondLiftTrimmingTransition))
                                (transition, Left(nextContext))
                            case None =>
                                val transition = ParsingCtxTransition(
                                    Some(firstExpandTransition, firstLiftTransition),
                                    Some(firstLiftTrimmingTransition, revertTransition),
                                    None)
                                // TODO unexpected input 맞나?
                                (transition, Right(UnexpectedInput(input)))
                        }
                    case Some(_) => throw new AssertionError("")
                    case None =>
                        val transition = ParsingCtxTransition(Some(firstExpandTransition, firstLiftTransition), None, None)
                        liftedGraph0pre.results.of(startNode) match {
                            case Some(result) =>
                                // 파싱 결과는 있는데 더이상 진행할 수 없는 경우
                                (transition, Left(ParsingCtx(
                                    nextGen,
                                    CtxGraph(Set(), Set(), Results(startNode -> result), Results()),
                                    Set())))
                            case None =>
                                (transition, Right(UnexpectedInput(input)))
                        }
                }
            }
        }

        def proceed(input: ConcreteInput): Either[ParsingCtx, ParsingError] = proceedDetail(input)._2
    }

    val initialContext = {
        val emptyResult: Results[Node, R] = {
            val baseResults = derivationFunc.derive(startNode).baseResults mapValues { _.result }
            if (baseResults.isEmpty) Results[Node, R]() else Results[Node, R](startNode -> baseResults)
        }
        ParsingCtx(
            0,
            CtxGraph[R](
                Set(startNode),
                Set(),
                emptyResult,
                Results[SequenceNode, R]()),
            Set(startNode))
    }

    def parse(source: Inputs.ConcreteSource): Either[ParsingCtx, ParsingError] =
        source.foldLeft[Either[ParsingCtx, ParsingError]](Left(initialContext)) {
            (ctx, input) =>
                ctx match {
                    case Left(ctx) => ctx.proceed(input)
                    case error @ Right(_) => error
                }
        }
    def parse(source: String): Either[ParsingCtx, ParsingError] =
        parse(Inputs.fromString(source))
}

class NaiveParser[R <: ParseResult](grammar: Grammar, resultFunc: ParseResultFunc[R], derivationFunc: DerivationSliceFunc[R]) extends NewParser(grammar, resultFunc, derivationFunc) with DeriveTasks[R, CtxGraph[R]] {
    class NaiveParsingCtx(gen: Int, graph: Graph) extends ParsingCtx(gen, graph, Set()) {
        override def proceedDetail(input: ConcreteInput): (ParsingCtxTransition, Either[NaiveParsingCtx, ParsingError]) = {
            val nextGen = gen + 1

            val termFinishingTasks = graph.nodes collect {
                case node @ TermNode(term, _) if term accept input => FinishingTask(nextGen, node, TermResult(resultFunc.terminal(input)), Condition.True)
            }

            if (termFinishingTasks.isEmpty) {
                (ParsingCtxTransition(None, None, None), Right(UnexpectedInput(input)))
            } else {
                val liftedGraph0 = rec(termFinishingTasks.toList, graph.withNoResults.asInstanceOf[Graph])
                val newTermNodes: Set[Node] = liftedGraph0.nodes collect { case node @ TermNode(_, `nextGen`) => node }

                val firstExpandTransition = ExpandTransition(s"Gen $gen > (1) - No Expansion", graph, graph, Set())
                val firstLiftTransition = LiftTransition(s"Gen $gen > (2) First Lift", graph, liftedGraph0, termFinishingTasks.asInstanceOf[Set[NewParser[R]#Task]], Set())

                liftedGraph0.subgraphIn(startNode, newTermNodes, resultFunc) match {
                    case Some(liftedGraph: Graph) =>
                        val firstLiftTrimmingTransition = TrimmingTransition(s"Gen $gen > (3) First Trimming", liftedGraph0, liftedGraph, startNode, newTermNodes)
                        // 4.revert
                        val revertedGraph: Graph = ParsingCtx.revert(gen, liftedGraph, liftedGraph.results, liftedGraph.nodes)
                        val revertTransition = RevertTransition(s"Gen $gen > (4) Revert", liftedGraph, revertedGraph, liftedGraph, liftedGraph.results)

                        // 7. 2차 트리밍
                        val finalGraphOpt = revertedGraph.subgraphIn(startNode, newTermNodes, resultFunc) map { _.asInstanceOf[Graph] }
                        finalGraphOpt match {
                            case Some(finalGraph) =>
                                val secondLiftTrimmingTransition = TrimmingTransition(s"Gen $gen > (5) Second Trimming", revertedGraph, finalGraph, startNode, newTermNodes)
                                val nextContext = new NaiveParsingCtx(nextGen, finalGraph)
                                val transition = ParsingCtxTransition(
                                    Some(firstExpandTransition, firstLiftTransition),
                                    Some(firstLiftTrimmingTransition, revertTransition),
                                    Some(secondLiftTrimmingTransition))
                                (transition, Left(nextContext))
                            case None =>
                                val transition = ParsingCtxTransition(
                                    Some(firstExpandTransition, firstLiftTransition),
                                    Some(firstLiftTrimmingTransition, revertTransition),
                                    None)
                                // TODO unexpected input 맞나?
                                (transition, Right(UnexpectedInput(input)))
                        }
                    case Some(_) => throw new AssertionError("")
                    case None =>
                        val transition = ParsingCtxTransition(Some(firstExpandTransition, firstLiftTransition), None, None)
                        liftedGraph0.results.of(startNode) match {
                            case Some(result) =>
                                // 파싱 결과는 있는데 더이상 진행할 수 없는 경우
                                (transition, Left(new NaiveParsingCtx(
                                    nextGen,
                                    CtxGraph(Set(), Set(), Results(startNode -> result), Results()))))
                            case None =>
                                (transition, Right(UnexpectedInput(input)))
                        }
                }
            }
        }

        override def proceed(input: ConcreteInput): Either[ParsingCtx, ParsingError] = proceedDetail(input)._2
    }

    def process(task: Task, cc: CtxGraph[R]): (CtxGraph[R], Seq[Task]) = {
        task match {
            case task: DeriveTask => deriveTask(task, cc)
            case task: FinishingTask => finishingTask(task, cc)
            case task: SequenceProgressTask => sequenceProgressTask(task, cc)
        }
    }

    def rec(tasks: List[Task], cc: CtxGraph[R]): CtxGraph[R] =
        tasks match {
            case task +: rest =>
                val (newCC, newTasks) = process(task, cc)
                rec((newTasks.toList ++ rest).distinct, newCC)
            case List() => cc
        }

    override val initialContext = {
        val initialGraph = rec(List(DeriveTask(0, startNode)), CtxGraph(Set(startNode), Set(), Results(), Results()))
        new NaiveParsingCtx(0, initialGraph)
    }
}
