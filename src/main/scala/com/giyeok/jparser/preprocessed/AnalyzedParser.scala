package com.giyeok.jparser.preprocessed

import com.giyeok.jparser.Grammar
import com.giyeok.jparser.Inputs
import com.giyeok.jparser.Kernels
import com.giyeok.jparser.Symbols
import com.giyeok.jparser.ParseTree
import com.giyeok.jparser.Parser
import com.giyeok.jparser.preprocessed.PreprocessedParserSpec.KernelSet
import com.giyeok.jparser.Symbols._
import com.giyeok.jparser.Inputs._
import com.giyeok.jparser.Kernels._
import com.giyeok.jparser.ParseTree._
import PreprocessedParserSpec._

class AnalyzedParser(grammar: Grammar) extends Parser(grammar) {
    type IPNSymbol = NonAtomicSymbol with Nonterm

    val ctx0 = initialContext

    class AnalyzedParsingContext(val ctx: ParsingContext) {
        val ipns: Set[NonAtomicSymbolProgress[IPNSymbol]] = ctx.allProceededEdges map { _.end.asInstanceOf[NonAtomicSymbolProgress[IPNSymbol]] }
        val ipnsByKernel: Map[NonAtomicNontermKernel[IPNSymbol], Set[NonAtomicSymbolProgress[IPNSymbol]]] = ipns groupBy { _.kernel }
        val kernelSet = ipnsByKernel.keySet

        lazy val termGroups: Set[TermGroupDesc] = {
            import Symbols.Terminals._

            val terminals = ctx.terminalNodes map { _.kernel.symbol }
            val charTerms: Set[CharacterTermGroupDesc] = terminals collect { case x: CharacterTerminal => TermGroupDesc.descOf(x) }
            val virtTerms: Set[VirtualTermGroupDesc] = terminals collect { case x: VirtualTerminal => TermGroupDesc.descOf(x) }

            val charIntersects: Set[CharacterTermGroupDesc] = charTerms flatMap { term1 =>
                charTerms collect {
                    case term2 if term1 != term2 => term1 intersect term2
                } filterNot { _.isEmpty }
            }
            val virtIntersects: Set[VirtualTermGroupDesc] = virtTerms flatMap { term1 =>
                virtTerms collect {
                    case term2 if term1 != term2 => term1 intersect term2
                } filterNot { _.isEmpty }
            }

            // charIntersects foreach { d => println(d.toShortString) }
            // virtIntersects foreach { d => println(d.toShortString) }

            val charTermGroups = (charTerms map { term =>
                charIntersects.foldLeft(term) { _ - _ }
            }) ++ charIntersects
            val virtTermGroups = (virtTerms map { term =>
                virtIntersects.foldLeft(term) { _ - _ }
            }) ++ virtIntersects

            charTermGroups ++ virtTermGroups
        }

        def cyclesFrom(kernel: NontermKernel[Nonterm]): Map[Kernel, Set[Seq[Kernel]]] = {
            def cycles(kernel: Kernel, path: Seq[Kernel], cc: Map[Kernel, Set[Seq[Kernel]]]): Map[Kernel, Set[Seq[Kernel]]] = {
                kernel match {
                    case kernel: NontermKernel[Nonterm] =>
                        val idx = path indexOf kernel
                        if (idx >= 0) {
                            assert(path(idx) == kernel)
                            cc + (kernel -> (cc.get(kernel).getOrElse(Set()) + path.slice(idx + 1, path.length)))
                        } else {
                            val (edges, reverters) = kernel.derive(grammar)
                            edges.foldLeft(cc) { (cc, edge) =>
                                edge match {
                                    case SimpleEdgeTmpl(next) =>
                                        cycles(next, path :+ kernel, cc)
                                    case JoinEdgeTmpl(next, join, _) =>
                                        cycles(next, path :+ kernel, cycles(join, path :+ kernel, cc))
                                }
                            }
                        }
                    case _ => cc
                }
            }
            cycles(kernel, Seq(), Map())
        }

        def reachable(kernel: NontermKernel[Nonterm], terminals: Set[TerminalNode]): OriginExpand = {
            val cycles = cyclesFrom(kernel)
            ???
        }

        def actionFor(termGroup: TermGroupDesc): PreprocessedParserSpec.KernelSetAction = {
            assert(termGroups contains termGroup)
            val activatedTerminals = ctx.terminalNodes filter { _.kernel.symbol.accept(AbstractInput(termGroup)) }
            // 각 ipn에서 activatedTerminals로 도달하는 subgraph들을 추려서 OriginExpand를 만든다
            ipns map { ipn =>
                reachable(ipn.kernel, activatedTerminals)
            }
            ???
        }
    }

    // TODO 구체적인 Node 없이 Kernel로만 derive하는 것도 만들어야겠다
    //   - 그래서 얻어낸 Kernel들 중에 TerminalKernel들로 TermGroupDesc를 만들어야지

    private val knownKernelSetActions = scala.collection.mutable.Map[KernelSet, Map[TermGroupDesc, Option[KernelSetAction]]]()
    // Option[KernelSetAction]이 None이면 아직 처리를 안했다는 의미

    def expand0(kernelSet: KernelSet, term: TermGroupDesc): KernelSetAction = {
        val ksAction: KernelSetAction = ???
        if (!(knownKernelSetActions contains kernelSet)) {
            knownKernelSetActions(kernelSet) = ??? // kernelSet으로부터 derive해서 얻어낸 TerminalKernel들로 TermGroupDesc를 구한다
        }
        ksAction
    }
}
