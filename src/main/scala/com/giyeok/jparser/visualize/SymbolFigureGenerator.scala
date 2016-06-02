package com.giyeok.jparser.visualize

import com.giyeok.jparser.Grammar
import com.giyeok.jparser.Symbols
import com.giyeok.jparser.Symbols._
import java.lang.Character.UnicodeBlock
import FigureGenerator.Spacing

class SymbolFigureGenerator[Fig](g: FigureGenerator.Generator[Fig], ap: FigureGenerator.Appearances[Fig]) {
    def symbolFig(symbol: Symbol): Fig = {
        def join(list: List[Fig], joining: => Fig): List[Fig] = list match {
            case head +: List() => List(head)
            case head +: next +: List() => List(head, joining, next)
            case head +: next +: rest => head +: joining +: join(next +: rest, joining)
        }

        def needParentheses(symbol: Symbol): Boolean =
            symbol match {
                case _@ (Nonterminal(_) | Terminals.ExactChar(_) | Sequence(Seq(Terminals.ExactChar(_)), _) | Empty) => false
                case _ => true
            }

        def exactCharacterRepr(char: Char): String = char match {
            case c if 33 <= c && c <= 126 => c.toString
            case '\n' => "\\n"
            case '\r' => "\\r"
            case '\t' => "\\t"
            case c => f"\\u$c%04x"
        }
        def rangeCharactersRepr(start: Char, end: Char): (String, String) =
            (exactCharacterRepr(start), exactCharacterRepr(end))

        symbol match {
            case Terminals.ExactChar(c) => g.textFig(exactCharacterRepr(c), ap.terminal)
            case chars: Terminals.Chars =>
                g.horizontalFig(Spacing.None, join(chars.groups map {
                    case (f, t) if f == t => g.textFig(exactCharacterRepr(f), ap.terminal)
                    case (f, t) =>
                        val (rangeStart: String, rangeEnd: String) = rangeCharactersRepr(f, t)
                        g.horizontalFig(Spacing.None, Seq(g.textFig(rangeStart, ap.terminal), g.textFig("-", ap.default), g.textFig(rangeEnd, ap.terminal)))
                }, g.textFig("|", ap.default)))
            case t: Terminal => g.textFig(t.toShortString, ap.terminal)
            case Empty => g.textFig("ε", ap.nonterminal)
            case Start => g.textFig("Start", ap.default)
            case Nonterminal(name) => g.textFig(name, ap.nonterminal)
            case Sequence(seq, ws) =>
                if (seq.isEmpty) {
                    g.horizontalFig(Spacing.Medium, Seq())
                } else {
                    def adjExChars(list: List[Terminals.ExactChar]): Fig =
                        g.horizontalFig(Spacing.None, list map { symbolFig(_) })
                    val grouped = seq.foldRight((List[Fig](), List[Terminals.ExactChar]())) {
                        ((i, m) =>
                            i match {
                                case terminal: Terminals.ExactChar => (m._1, terminal +: m._2)
                                case symbol if m._2.isEmpty => (symbolFig(symbol) +: m._1, List())
                                case symbol => (symbolFig(symbol) +: adjExChars(m._2) +: m._1, List())
                            })
                    }
                    g.horizontalFig(Spacing.Medium, if (grouped._2.isEmpty) grouped._1 else adjExChars(grouped._2) +: grouped._1)
                }
            case OneOf(syms) =>
                g.horizontalFig(Spacing.None, join((syms map { sym =>
                    if (needParentheses(sym)) g.horizontalFig(Spacing.None, Seq(g.textFig("(", ap.default), symbolFig(sym), g.textFig(")", ap.default)))
                    else symbolFig(sym)
                }).toList, g.textFig("|", ap.default)))
            case Repeat(sym, lower) =>
                val rep: String = lower match {
                    case 0 => "*"
                    case 1 => "+"
                    case n => s"$n+"
                }
                if (needParentheses(sym))
                    g.horizontalFig(Spacing.None, Seq(g.textFig("(", ap.default), symbolFig(sym), g.textFig(")" + rep, ap.default)))
                else g.horizontalFig(Spacing.None, Seq(symbolFig(sym), g.textFig(rep, ap.default)))
            case Except(sym, except) =>
                val symFig =
                    if (!needParentheses(sym)) symbolFig(sym)
                    else g.horizontalFig(Spacing.None, Seq(g.textFig("(", ap.default), symbolFig(sym), g.textFig(")", ap.default)))
                val exceptFig =
                    if (!needParentheses(except)) symbolFig(except)
                    else g.horizontalFig(Spacing.None, Seq(g.textFig("(", ap.default), symbolFig(except), g.textFig(")", ap.default)))

                g.horizontalFig(Spacing.Medium, Seq(symFig, g.textFig("except", ap.default), exceptFig))
            case LookaheadIs(lookahead) =>
                g.horizontalFig(Spacing.Small, Seq(g.textFig("(", ap.default), g.textFig("lookahead_is", ap.default), symbolFig(lookahead), g.textFig(")", ap.default)))
            case LookaheadExcept(except) =>
                g.horizontalFig(Spacing.Small, Seq(g.textFig("(", ap.default), g.textFig("lookahead_except", ap.default), symbolFig(except), g.textFig(")", ap.default)))
            case Proxy(sym) =>
                g.horizontalFig(Spacing.Small, Seq(g.textFig("P(", ap.default), symbolFig(sym), g.textFig(")", ap.default)))
            case Backup(sym, backup) =>
                g.verticalFig(Spacing.Small, Seq(symbolFig(sym), g.textFig("if failed", ap.default), symbolFig(backup)))
            case Join(sym, join) =>
                g.verticalFig(Spacing.Small, Seq(symbolFig(sym), g.textFig("&", ap.default), symbolFig(join)))
            case Longest(sym) =>
                g.horizontalFig(Spacing.Small, Seq(g.textFig("L(", ap.default), symbolFig(sym), g.textFig(")", ap.default)))
            case EagerLongest(sym) =>
                g.horizontalFig(Spacing.Small, Seq(g.textFig("EL(", ap.default), symbolFig(sym), g.textFig(")", ap.default)))
        }
    }
}
