package com.giyeok.moonparser.tests

import com.giyeok.moonparser.Grammar
import com.giyeok.moonparser.SymbolHelper._
import scala.collection.immutable.ListMap
import com.giyeok.moonparser.Parser
import org.scalatest.junit.AssertionsForJUnit
import com.giyeok.moonparser.Inputs._
import com.giyeok.moonparser.Symbols.Symbol
import com.giyeok.moonparser.Inputs
import org.scalatest.FlatSpec
import scala.collection.immutable.ListSet
import com.giyeok.moonparser.Parser

object SimpleGrammar5 extends Grammar with StringSamples {
    val name = "Simple Grammar 5"
    val rules: RuleMap = ListMap(
        "S" -> ListSet(seq(n("A"), n("B"))),
        "A" -> ListSet(i("a"), e),
        "B" -> ListSet(i("b"), e))
    val startSymbol = n("S")

    val correctSamples = Set("", "ab")
    val incorrectSamples = Set("aa")

    def main(args: Array[String]): Unit = {
        val parser = new Parser(this)
        val ctx = parser.startingContext
        ctx.graph.edges foreach {
            _ match {
                case e: parser.SimpleEdge =>
                    println(s"${e.from.toShortString} => ${e.to.toShortString}")
                case e: parser.EagerAssassinEdge =>
                    println(s"${e.from.toShortString} =(assassin)=> ${e.to.toShortString}")
            }
        }
        println("=== End ===")
    }
}

object SimpleGrammar6 extends Grammar with StringSamples {
    val name = "Simple Grammar 6"
    val rules: RuleMap = ListMap(
        "S" -> ListSet(seq(n("A"), n("C"))),
        "A" -> ListSet(seq(n("B"), i("a").star)),
        "B" -> ListSet(i("b"), e),
        "C" -> ListSet(seq(n("B"), i("c").star)))
    val startSymbol = n("S")

    val correctSamples = Set("", "ab")
    val incorrectSamples = Set("aa")
}

object SimpleGrammarSet3 {
    val grammars: Set[Grammar with Samples] = Set(
        SimpleGrammar5,
        SimpleGrammar6)
}

class SimpleGrammar3TestSuite extends BasicParseTest(SimpleGrammarSet3.grammars)
