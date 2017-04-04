package com.giyeok.jparser.nparser

import com.giyeok.jparser.Inputs.Input
import com.giyeok.jparser.ParseResult
import com.giyeok.jparser.ParseResultFunc
import com.giyeok.jparser.nparser.AcceptCondition.AcceptCondition
import com.giyeok.jparser.nparser.NGrammar._
import com.giyeok.jparser.nparser.ParsingContext._

class ParseTreeConstructor[R <: ParseResult](resultFunc: ParseResultFunc[R])(grammar: NGrammar)(input: Seq[Input], val history: Seq[Graph], conditionFinal: Map[AcceptCondition, Boolean]) {
    // conditionFinal foreach { kv => println(s"${kv._1} -> ${kv._2}") }
    sealed trait KernelEdge { val start: Kernel }
    case class SimpleKernelEdge(start: Kernel, end: Kernel) extends KernelEdge
    case class JoinKernelEdge(start: Kernel, end: Kernel, join: Kernel) extends KernelEdge

    case class KernelGraph(nodes: Set[Kernel], edges: Set[KernelEdge]) {
        val edgesByStart: Map[Kernel, Set[KernelEdge]] = {
            val edgesMap = edges groupBy { _.start }
            ((nodes -- edgesMap.keySet) map { n => n -> Set[KernelEdge]() }).toMap ++ edgesMap
        }
    }

    val finishes: Vector[KernelGraph] = {
        (history map { graph =>
            val filteredGraph = graph filterNode { node => conditionFinal(node.condition) }
            val kernelNodes: Set[Kernel] = filteredGraph.nodes map { _.kernel }
            val kernelEdges: Set[KernelEdge] = filteredGraph.edges map {
                case SimpleEdge(start, end) => SimpleKernelEdge(start.kernel, end.kernel)
                case JoinEdge(start, end, join) => JoinKernelEdge(start.kernel, end.kernel, join.kernel)
            }
            KernelGraph(kernelNodes, kernelEdges)
        }).toVector
    }
    // TODO finishes의 node set을 symbolId 기준으로 정렬해 놓으면 더 빠르게 할 수 있을듯

    def reconstruct(): Option[R] = {
        reconstruct(Kernel(grammar.startSymbol, 1, 0, input.length)(grammar.nsymbols(grammar.startSymbol)), input.length)
    }
    def reconstruct(kernel: Kernel, gen: Int): Option[R] = {
        if (kernel.pointer > 0 && (finishes(gen).nodes contains kernel)) Some(reconstruct(kernel, gen, Set())) else None
    }

    private def reconstruct(kernel: Kernel, gen: Int, traces: Set[(Int, Int)]): R = {
        // println("reconstruct", kernel, gen, traces)
        assert(finishes(gen).nodes contains kernel)
        assert(kernel.endGen == gen)

        def reconstruct0(child: Kernel, childGen: Int): R = {
            val newTraces: Set[(Int, Int)] =
                if ((kernel.beginGen, gen) != (child.beginGen, childGen)) Set()
                else traces + ((kernel.symbolId, kernel.pointer))
            reconstruct(child, childGen, newTraces)
        }

        kernel.symbol match {
            case symbol: NAtomicSymbol if traces contains ((kernel.symbolId, kernel.pointer)) =>
                // println("cyclicBind?")
                resultFunc.cyclicBind(kernel.beginGen, gen, symbol.symbol)

            case symbol: Sequence if traces contains ((kernel.symbolId, kernel.pointer)) =>
                // println(s"sequence cyclicBind - $kernel")
                resultFunc.sequence(kernel.beginGen, gen, symbol.symbol, kernel.pointer)

            case Sequence(symbol, sequence) =>
                if (kernel.pointer == 0) {
                    assert(kernel.beginGen == kernel.endGen)
                    resultFunc.sequence(kernel.beginGen, kernel.endGen, symbol, 0)
                } else {
                    val (symbolId, prevPointer) = (kernel.symbolId, kernel.pointer - 1)
                    val prevKernels = finishes(gen).nodes filter { kern =>
                        (kern.symbolId == symbolId) && (kern.pointer == prevPointer) && (kern.beginGen == kernel.beginGen)
                    }
                    val trees = prevKernels flatMap { prevKernel =>
                        val childKernel = Kernel(sequence(prevPointer), 1, prevKernel.endGen, gen)(grammar.nsymbols(sequence(prevPointer)))
                        if (finishes(gen).nodes contains childKernel) {
                            val precedingTree = reconstruct0(Kernel(kernel.symbolId, prevPointer, kernel.beginGen, prevKernel.endGen)(kernel.symbol), prevKernel.endGen)
                            val childTree = reconstruct0(childKernel, gen)
                            // println(s"preceding: $precedingTree")
                            // println(s"child: $childTree")
                            Some(resultFunc.append(precedingTree, childTree))
                        } else None
                    }
                    val appendedSeq = resultFunc.merge(trees)
                    if (kernel.pointer == sequence.length) resultFunc.bind(kernel.beginGen, gen, symbol, appendedSeq) else appendedSeq
                }

            case Join(symbol, body, join) =>
                assert(kernel.pointer == 1)
                val bodyKernel = Kernel(body, 1, kernel.beginGen, kernel.endGen)(grammar.nsymbols(body))
                val joinKernel = Kernel(join, 1, kernel.beginGen, kernel.endGen)(grammar.nsymbols(join))
                val bodyTree = reconstruct0(bodyKernel, kernel.endGen)
                val joinTree = reconstruct0(joinKernel, kernel.endGen)
                resultFunc.join(kernel.beginGen, kernel.endGen, symbol, bodyTree, joinTree)

            case Terminal(symbol) =>
                resultFunc.bind(kernel.beginGen, kernel.endGen, symbol,
                    resultFunc.terminal(kernel.beginGen, input(kernel.beginGen)))

            case symbol: NAtomicSymbol =>
                assert(kernel.pointer == 1)
                val prevKernel = Kernel(kernel.symbolId, 0, kernel.beginGen, kernel.beginGen)(kernel.symbol)
                assert(finishes(gen).edgesByStart(prevKernel) forall { _.isInstanceOf[SimpleKernelEdge] })
                val bodyKernels = finishes(gen).edgesByStart(prevKernel) collect {
                    case SimpleKernelEdge(_, end) if end.endGen == gen && end.isFinished => end
                }
                val bodyTrees = bodyKernels map { bodyKernel =>
                    reconstruct0(bodyKernel, kernel.endGen)
                }
                assert(bodyTrees.nonEmpty)
                resultFunc.bind(kernel.beginGen, kernel.endGen, symbol.symbol, resultFunc.merge(bodyTrees))
        }
    }
}
