package com.giyeok.jparser.nparser

import com.giyeok.jparser.Grammar
import com.giyeok.jparser.Symbols
import com.giyeok.jparser.nparser.NGrammar._

// Numbered Grammar
class NGrammar(val nsymbols: Map[Int, NGrammar.NAtomicSymbol], val nsequences: Map[Int, NGrammar.Sequence], val startSymbol: Int) {
    def symbolOf(id: Int): NGrammar.NSymbol = {
        nsymbols get id match {
            case Some(nsymbol) => nsymbol
            case None => nsequences(id)
        }
    }
}

object NGrammar {
    sealed trait NSymbol { val symbol: Symbols.Symbol }
    sealed trait NAtomicSymbol extends NSymbol

    case class Terminal(symbol: Symbols.Terminal) extends NAtomicSymbol

    sealed trait NSimpleDerivable extends NAtomicSymbol { val produces: Set[Int] }
    case class Start(produces: Set[Int]) extends NSimpleDerivable { val symbol = Symbols.Start }
    case class Nonterminal(symbol: Symbols.Nonterminal, produces: Set[Int]) extends NSimpleDerivable
    case class OneOf(symbol: Symbols.OneOf, produces: Set[Int]) extends NSimpleDerivable
    case class Proxy(symbol: Symbols.Proxy, produce: Int) extends NSimpleDerivable { val produces = Set(produce) }
    case class Repeat(symbol: Symbols.Repeat, produces: Set[Int]) extends NSimpleDerivable
    case class Except(symbol: Symbols.Except, body: Int, except: Int) extends NSimpleDerivable { val produces = Set(body, except) }
    case class Longest(symbol: Symbols.Longest, body: Int) extends NSimpleDerivable { val produces = Set(body) }
    case class EagerLongest(symbol: Symbols.EagerLongest, body: Int) extends NSimpleDerivable { val produces = Set(body) }

    case class Join(symbol: Symbols.Join, body: Int, join: Int) extends NAtomicSymbol

    sealed trait NLookaheadSymbol extends NAtomicSymbol {
        val symbol: Symbols.Lookahead
        val lookahead: Int
    }
    case class LookaheadIs(symbol: Symbols.LookaheadIs, lookahead: Int) extends NLookaheadSymbol
    case class LookaheadExcept(symbol: Symbols.LookaheadExcept, lookahead: Int) extends NLookaheadSymbol

    // case class Compaction(symbols: Seq[Int]) extends NSymbol

    case class Sequence(symbol: Symbols.Sequence, sequence: Seq[Int]) extends NSymbol

    def fromGrammar(grammar: Grammar): NGrammar = {
        var newId = 0
        val symbolsMap = scala.collection.mutable.Map[Symbols.Symbol, Int]()
        val nsymbols = scala.collection.mutable.Map[Int, NAtomicSymbol]()
        val nsequences = scala.collection.mutable.Map[Int, Sequence]()

        def numberOf(symbol: Symbols.Symbol): Int =
            symbolsMap get symbol match {
                case Some(nsymbolId) => nsymbolId
                case None =>
                    newId += 1
                    val myId = newId
                    symbolsMap(symbol) = myId
                    symbol match {
                        case symbol: Symbols.AtomicSymbol =>
                            nsymbols(myId) = symbol match {
                                case symbol: Symbols.Terminal => Terminal(symbol)

                                case Symbols.Start => Start(Set(numberOf(grammar.startSymbol)))
                                case symbol: Symbols.Nonterminal => Nonterminal(symbol, grammar.rules(symbol.name) map { numberOf _ })
                                case symbol: Symbols.OneOf => OneOf(symbol, symbol.syms map { numberOf _ })
                                case symbol: Symbols.Proxy => Proxy(symbol, numberOf(symbol.sym))
                                case symbol: Symbols.Repeat => Repeat(symbol, Set(numberOf(symbol.baseSeq), numberOf(symbol.repeatSeq)))
                                case symbol: Symbols.Except => Except(symbol, numberOf(symbol.sym), numberOf(symbol.except))
                                case symbol: Symbols.Longest => Longest(symbol, numberOf(symbol.sym))
                                case symbol: Symbols.EagerLongest => EagerLongest(symbol, numberOf(symbol.sym))

                                case symbol: Symbols.Join => Join(symbol, numberOf(symbol.sym), numberOf(symbol.join))

                                case symbol: Symbols.LookaheadIs => LookaheadIs(symbol, numberOf(symbol.lookahead))
                                case symbol: Symbols.LookaheadExcept => LookaheadExcept(symbol, numberOf(symbol.except))
                            }
                        case symbol: Symbols.Sequence =>
                            nsequences(myId) = Sequence(symbol, symbol.seq map { numberOf _ })
                            assert(symbol.seq forall { symbolsMap contains _ })
                    }
                    myId
            }
        val startSymbolId = numberOf(Symbols.Start)
        new NGrammar(nsymbols.toMap, nsequences.toMap, startSymbolId)
    }
}

class CompactNGrammar(nsymbols: Map[Int, NGrammar.NAtomicSymbol], nsequences: Map[Int, NGrammar.Sequence], startSymbol: Int) extends NGrammar(nsymbols, nsequences, startSymbol) {
    def isCompactable(id: Int): Boolean = {
        nsymbols get id match {
            case Some(nsymbol) =>
                nsymbol match {
                    case _: Start | _: Nonterminal | _: OneOf | _: Proxy | _: Repeat => true
                    case _ => false
                }
            case None =>
                nsequences(id).sequence.length == 1
        }
    }

    // (key, value)는 key가 finish되면 value의 심볼들도 모두 finish됨을 의미한다
    lazy val compactionMap: Map[Int, Set[Int]] = ???
    lazy val reverseCompactionMap: Map[Int, Set[Int]] = ???
}

object CompactNGrammar {
    def fromNGrammar(ngrammar: NGrammar): CompactNGrammar = new CompactNGrammar(ngrammar.nsymbols, ngrammar.nsequences, ngrammar.startSymbol)
}
