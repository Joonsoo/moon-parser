package com.giyeok.jparser.parsergen.nocond

import com.giyeok.jparser.Inputs.CharacterTermGroupDesc
import com.giyeok.jparser.nparser.NGrammar
import com.giyeok.jparser.nparser.NGrammar._
import com.giyeok.jparser.parsergen.TermGrouper

import scala.collection.mutable

// 현재 stack top이 replace로 바뀐 다음 following이 그 뒤에 붙음
// pendingFinishReplace.nonEmpty인 경우 following을 붙이는 대신 stack top을 finish할 수도 있었다는 것을 의미하는데,
// 만약 following이 다음 term을 받지 못해서 following을 스택에서 제거하고 pendingFinish를 진행하는 경우 stack top을
// pendingFinishReplace로 바꿔서 진행해야 함.
case class Following(following: AKernelSet, pendingFinishReplace: AKernelSet)

case class GraphChange(replacePrev: AKernelSet, following: Option[Following])

case class Memoize[T <: Equals, U]() {
    private val memo = mutable.Map[T, U]()

    def apply(param: T)(func: => U): U = memo get param match {
        case Some(saved) => saved
        case None =>
            val newValue = func
            memo(param) = newValue
            newValue
    }
}

class GrammarAnalyzer(val grammar: NGrammar) {
    lazy val nullableSymbols: Set[Int] = {
        val initialNullables: Set[Int] =
            (grammar.nsequences filter (_._2.sequence.isEmpty)).keySet ++
                (grammar.nsymbols filter (_._2.isInstanceOf[NLookaheadSymbol])).keySet

        def traverse(cc: Set[Int]): Set[Int] = {
            val newNullableSeqs = (grammar.nsequences filter {
                _._2.sequence forall cc.contains
            }).keySet -- cc
            val newNullableSyms = (grammar.nsymbols filter {
                _._2 match {
                    case simpleDerive: NSimpleDerive => (simpleDerive.produces intersect cc).nonEmpty
                    case except: NExcept => cc contains except.body
                    case join: NJoin => (cc contains join.body) && (cc contains join.join)
                    case longest: NLongest => cc contains longest.body
                    case _: NLookaheadSymbol => true
                    case _: NTerminal => false
                }
            }).keySet -- cc
            if (newNullableSeqs.isEmpty && newNullableSyms.isEmpty) cc else traverse(cc ++ newNullableSeqs ++ newNullableSyms)
        }

        traverse(initialNullables)
    }

    private val deriveGraphMemo = Memoize[AKernel, AKernelGraph]()
    private val memoReachableTermSymbolIds = Memoize[AKernel, Set[Int]]()

    def deriveGraphFrom(kernel: AKernel): AKernelGraph = deriveGraphMemo(kernel) {
        val initGraph = AKernelGraph(Set(), Set(), Map(), Map()).addNode(kernel)
        val simulation = new ParsingTaskSimulator(grammar).simulate(AKernelGraph.emptyGraph, initGraph, List(DeriveTask(kernel)))
        simulation.nextGraph
    }

    // NTerminal의 ID들을 return
    def reachableTermSymbolIdsFrom(kernel: AKernel): Set[Int] = memoReachableTermSymbolIds(kernel) {
        val deriveGraph = deriveGraphFrom(kernel)
        deriveGraph.nodes filter (k => grammar.symbolOf(k.symbolId).isInstanceOf[NTerminal]) map (_.symbolId)
    }

    def acceptableTerms(kernelSet: AKernelSet): Set[CharacterTermGroupDesc] = {
        val termSymbolIds = kernelSet.items flatMap reachableTermSymbolIdsFrom
        val termSymbols = termSymbolIds map (grammar.symbolOf(_).asInstanceOf[NTerminal].symbol)
        TermGrouper.termGroupsOf(termSymbols) map (_.asInstanceOf[CharacterTermGroupDesc])
    }

    // kernelSet에 term에 속한 글자가 들어왔을때 그래프의 변화.
    // - replace는 kernelSet의 커널들 중 term을 받을 수 있는 것들이고,
    // - append는 replace 커널셋 뒤에 붙을 커널셋.
    //   - append가 None이면 붙을 게 없고 kernelSet이 바로 finish된단 의미.
    //   - append가 None이 아니고 append._2는 finishable이란 의미로, true이면 append되는 kernelset이 finish 가능함을 의미
    // - 사실 termChanges는 edgeChanges에서 nextKernelSet이 reachableTermSymbolsFrom(kernelSet) 중 term을 받을 수 있는
    // 커널셋을 nextKernelSet으로 주었을 때 나오는 결과의 다른 형태
    def termChanges(kernelSet: AKernelSet, term: CharacterTermGroupDesc): GraphChange = {
        val termSymbols = kernelSet.items flatMap reachableTermSymbolIdsFrom filter { symbolId =>
            grammar.symbolOf(symbolId).asInstanceOf[NTerminal].symbol.accept(term)
        }
        val termKernelSets = termSymbols map { termSymbolId =>
            AKernel(termSymbolId, 0)
        }
        edgeChanges(kernelSet, AKernelSet(termKernelSets))
    }

    // prevKernelSet -> nextKernelSet 이 이어져 있을 때 nextKernelSet이 finish되는 경우의 graph change를 반환
    // - nextKernelSet은 finish(core jparser식 표현으로는 progress)되므로 즉시 탈락되고 prevKernelSet이 stack top이 된다고 봐야함
    // - replacePrev는 prevKernelSet 중 nextKernelSet 의 커널 중 하나라도 도달가능한 커널들만 추린 것.
    // - nextKernelSet의 모든 커널이 progress된 경우, progress/finish가 replacedPrevKernelSet까지 도달하지 못한 것들이
    //   있으면 그런 커널들이 following.following으로 반환
    // - 만약 following.follwing이 non empty인데 progress/finish가 replacedPrevKernelSet의 일부로 도달하는 경우, 그렇게
    //   도달해서 progress되어야 하는 커널들은 following.pendingFinishReplace으로 반환
    def edgeChanges(prevKernelSet: AKernelSet, nextKernelSet: AKernelSet): GraphChange = {
        // DeriveGraph에는 AKernel pointer=0으로 되어 있으므로
        val nextKernelSet0 = nextKernelSet.items map (k => AKernel(k.symbolId, 0))
        // replacePrev=prevKernelSet.items 중 nextKernelSet 중 하나로라도 도달 가능한 것.
        val deriveGraphs = (prevKernelSet.items map { prevKernel =>
            // 필요한건 deriveGraphFrom(prevKernel)에서 nextKernelSet로 도달 가능한 노드들만 추린 subgraph지만 굳이..?
            prevKernel -> deriveGraphFrom(prevKernel)
        }).toMap
        val replacePrev = (deriveGraphs filterNot { p =>
            (p._2.nodes intersect nextKernelSet0).isEmpty
        }).keySet

        val mergedSubgraph = deriveGraphs.values.foldLeft(AKernelGraph.emptyGraph) { (m, i) =>
            m.merge(i)
        }
        // mergedSubgraph에서 nextKernelSet에 속한 커널들이 progress된다고 가정했을 때 발생할 progress operation들을 수집
        // 그중 replacePrev에 대한 progress인 것(실제로 progress된 이후, 즉 pointer+=1된 것들) -> following.following
        // 아닌 것들에 대한 progress(progress되기 전 상태로) -> following.pendingFinishReplace
        // 만약 following.following.isEmpty이면 replacePrev == following.pendingFinishReplace일 것이고, following을 None으로 리턴
        //   --> 이 때 replacePrev == following.pendingFinishReplace가 맞나?

        val validNextKernels = nextKernelSet.items filter (k => mergedSubgraph.nodes contains AKernel(k.symbolId, 0))
        val initiatingProgressTasks = validNextKernels.toList map ProgressTask
        val simulation = new ParsingTaskSimulator(grammar).simulateProgress(mergedSubgraph, initiatingProgressTasks)

        // simulationResult.progressTasks에서 initiatingProgressTasks는 제외
        val progressTasks = simulation.progressTasks
        val (pendingFinishReplace, appending) = progressTasks map (_.node) partition replacePrev.contains
        // appending에는 simulationResult.nullableProgressTasks 중 sequence progress 추가
        val seqAppending = appending filter { k =>
            grammar.symbolOf(k.symbolId).isInstanceOf[NSequence]
        }

        val notFinishedSeqAppendings = seqAppending map { k =>
            AKernel(k.symbolId, k.pointer + 1)
        } filter { k =>
            k.pointer < grammar.lastPointerOf(k.symbolId)
        }
        val following = if (notFinishedSeqAppendings.isEmpty) {
            assert(replacePrev == pendingFinishReplace)
            // assert(nullableAppending.isEmpty)
            None
        } else {
            Some(Following(AKernelSet(notFinishedSeqAppendings), AKernelSet(pendingFinishReplace)))
        }

        GraphChange(AKernelSet(replacePrev), following)
    }
}
