package com.giyeok.moonparser.dynamic

import com.giyeok.moonparser.Grammar
import com.giyeok.moonparser.ParserInputs._
import com.giyeok.moonparser.InputPieces._
import com.giyeok.moonparser.ParsedSymbols._
import com.giyeok.moonparser.GrElems._
import com.giyeok.moonparser.AmbiguousGrammarException

trait BlackboxParser {
    def parse(input: ParserInput): Parser.Result
    def parse(input: String): Parser.Result
}
object Parser {
    type Result = Set[ParsePossibility]
    object Result {
        implicit class ResultHelper(set: Result) {
            lazy val resultOpt = if (set.size == 1) Some(set.toSeq.head) else None
            lazy val parsedOpt = resultOpt match {
                case Some(Succeed(parsed)) => Some(parsed)
                case _ => None
            }
            lazy val succeed = parsedOpt.isDefined
            lazy val ambiguous = set.size > 1
        }
    }
    sealed abstract class ParsePossibility
    case class Failed(reason: FailedReason.Value, location: Int, message: String = "") extends ParsePossibility {
        override lazy val hashCode = (reason, location, message).hashCode
        override def equals(other: Any) = other match {
            case that: Failed =>
                (that canEqual this) && (reason == that.reason) && (location == that.location) && (message == that.message)
            case _ => false
        }
        def canEqual(other: Any) = other.isInstanceOf[Failed]
    }
    case class Succeed(parsed: ParsedSymbol) extends ParsePossibility {
        override lazy val hashCode = parsed.hashCode
        override def equals(other: Any) = other match {
            case that: Succeed => (that canEqual this) && (parsed == that.parsed)
            case _ => false
        }
        def canEqual(other: Any) = other.isInstanceOf[Succeed]
    }

    object FailedReason extends Enumeration {
        val UnexpectedEndOfFile = Value
    }
}
class BasicBlackboxParser(val grammar: Grammar) extends BlackboxParser {
    // === parser ===
    def parse(input: ParserInput): Parser.Result = {
        val parser = new Parser(grammar, input)
        parser.parseAll
    }
    def parse(input: String): Parser.Result = parse(ParserInput.fromString(input))
}
class Parser(val grammar: Grammar, val input: ParserInput)
        extends ParsingItems with IsNullable with OctopusStacks {
    private var _result: Parser.Result = Set()
    def result = _result

    protected val starter =
        EntryGroup(Set(Entry(grammar.n(grammar.startSymbol).toParsingItem, None)), StartSymbol, 0)
    protected val stack = new OctopusStack(starter)

    type ParserStep = (EntryGroup, ParsedSymbol) => Boolean

    def proceed(entry: EntryGroup, sym: ParsedSymbol, nextPointer: Int)(failed: ParserStep): Boolean =
        entry.proceed(sym, nextPointer) match {
            case Some(proceeded) => { stack.add(entry, proceeded); true }
            case None => failed(entry, sym)
        }
    def proceed(entry: EntryGroup, sym: ParsedSymbol)(failed: ParserStep): Boolean =
        proceed(entry, sym, entry.pointer)(failed)

    def defaultAmbiguousHandler(entry: EntryGroup, sym: ParsedSymbol): Boolean = {
        throw AmbiguousGrammarException(s"ambiguous at ${entry.pointer}")
        false
    }
    def finish(entry: EntryGroup, sym: ParsedSymbol)(failed: ParserStep, ambiguous: ParserStep = defaultAmbiguousHandler): Boolean = {
        println(s"Finish ${System.identityHashCode(entry).toHexString}")
        def finishItem(genpoint: Option[EntryGroup], reduced: ParsedSymbol): Boolean =
            genpoint match {
                case Some(genpoint) =>
                    println(s"Genpoint: ${System.identityHashCode(genpoint).toHexString}")
                    val proceeded = genpoint.proceed(reduced, entry.pointer)
                    if (proceeded.isEmpty) println(s"assertion: $reduced at ${System.identityHashCode(genpoint).toHexString}")
                    assert(proceeded.isDefined)
                    stack.add(genpoint, proceeded.get)
                    true
                case None =>
                    // parsing may be finished
                    // it succeed if entry reaches to the end of input, and failed otherwise
                    if (input finishedAt entry.pointer) _result += Parser.Succeed(entry.symbol)
                    else _result += Parser.Failed(Parser.FailedReason.UnexpectedEndOfFile, entry.pointer)
                    true
            }
        entry.finish.toSeq match {
            case Seq(finitem) => finishItem(finitem._1, finitem._2)
            case Nil => failed(entry, sym)
            case many =>
                // the grammar seems to be ambiguous
                // Generally, this is a grammar error
                // but the user may want to process it in different way
                ambiguous(entry, sym)
        }
    }

    def backup(entry: EntryGroup, sym: ParsedSymbol)(failed: ParserStep): Boolean = ???

    def defaultParseStep(entry: EntryGroup, sym: ParsedSymbol, nextPointer: Int): Boolean =
        proceed(entry, sym, nextPointer)((entry, sym) =>
            finish(entry, sym)((entry, sym) =>
                backup(entry, sym)((_, _) => false),
                (entry, _) => throw AmbiguousGrammarException(s"ambiguous at ${entry.pointer}")))

    def parseStep() =
        if (stack hasNext) {
            val entry = stack.pop()
            val sym = TermSymbol(input at entry.pointer, entry.pointer)

            // There could be several variations here
            // `proceed` and `finish` dual
            // `proceed`, and `finish` if failed

            println(s"TermSymbol $sym ${input finishedAt sym.pointer}")
            defaultParseStep(entry, sym, entry.pointer + 1)
        } else {
            false
        }
    def parseAll() = {
        while (parseStep()) ()
        result
    }

    case class EntryGroup(
            // parent is not Entry's problem, but it's OctopusStack's thing
            // genpoint will be changed if the item is created by `subs` of ParsingItem
            // and preserved if the item is created by `proceed` of ParsingItem
            kernels: Set[Entry],
            symbol: ParsedSymbol,
            pointer: Int) {

        lazy val members: Set[Entry] = {
            def stabilize(members: Set[Entry]): Set[Entry] = {
                val subs = members ++ (members flatMap { _.subs(this) })
                if (subs == members) subs else stabilize(subs)
            }
            stabilize(kernels)
        }

        def printMe() = {
            println(s"=== new EntryGroup ${System.identityHashCode(this).toHexString} ===")
            symbol match {
                case cs: ConcreteSymbol => println(s"Symbol: ${cs.text}")
                case _ =>
            }
            println(s"Pointer: $pointer")
            def printItem(e: Entry) =
                println(s"${e.item}: ${
                    e.genpoint match {
                        case Some(x) => System.identityHashCode(x).toHexString
                        case None => "root"
                    }
                } ${e.finish.isDefined}")
            println(s"Kernels: ${kernels.size}")
            kernels foreach printItem
            println(s"Members: ${members.size}")
            (members -- kernels) foreach printItem
            println("================================")
        }
        printMe()

        def proceed(sym: ParsedSymbol, pointer: Int): Option[EntryGroup] = {
            println(s"Proceed ${System.identityHashCode(this).toHexString} $sym")
            val proceeded = members flatMap { _.proceed(sym) }
            println(proceeded)
            if (proceeded.isEmpty) None else Some(EntryGroup(proceeded, sym, pointer))
        }
        lazy val finish: Set[(Option[EntryGroup], ParsedSymbol)] = {
            val fin = members flatMap { _.finish }
            println(s"Finish $fin")
            fin
        }
    }
    case class Entry(item: ParsingItem, genpoint: Option[EntryGroup]) {
        def subs(genpoint: EntryGroup): Set[Entry] = item.subs map { Entry(_, Some(genpoint)) }
        def proceed(sym: ParsedSymbol): Option[Entry] = {
            (item proceed sym) match {
                case Some(proceeded) =>
                    println(s"proceeded: $proceeded")
                    Some(Entry(proceeded, genpoint))
                case None => None
            }
        }
        lazy val finish: Option[(Option[EntryGroup], ParsedSymbol)] = item.finish match {
            case Some(fin) => Some(genpoint, fin)
            case None => None
        }
    }
}
