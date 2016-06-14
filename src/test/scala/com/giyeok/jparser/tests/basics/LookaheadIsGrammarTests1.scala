package com.giyeok.jparser.tests.basics

import com.giyeok.jparser.Grammar
import com.giyeok.jparser.tests.Samples
import com.giyeok.jparser.tests.BasicParseTest
import com.giyeok.jparser.tests.StringSamples
import scala.collection.immutable.ListMap
import scala.collection.immutable.ListSet
import com.giyeok.jparser.GrammarHelper._
import com.giyeok.jparser.tests.GrammarTestCases

object LookaheadIsGrammar1 extends Grammar with GrammarTestCases with StringSamples {
    val name = "LookaheadIsGrammar 1"
    val rules: RuleMap = ListMap(
        "S" -> ListSet(n("A").star),
        "A" -> ListSet(
            seq(longest(chars('a' to 'z').plus), lookahead_is(c(' '))),
            chars(" ")))
    val startSymbol = n("S")

    val grammar = this
    val correctSamples = Set[String]("abc ", "abc def ")
    val incorrectSamples = Set[String]("abc")
}

object LookaheadIsGrammar2 extends Grammar with GrammarTestCases with StringSamples {
    val name = "LookaheadIsGrammar 2"
    val rules: RuleMap = ListMap(
        "S" -> ListSet(
            seq(n("B"), chars('a' to 'z').star)),
        "B" -> ListSet(
            seq(i("abc"), lookahead_is(i("def")))))
    val startSymbol = n("S")

    val grammar = this
    val correctSamples = Set[String]("abcdef")
    val incorrectSamples = Set[String]("")
}

object GrammarWithLookaheadIs {
    // Grammar 1, 2, 7 are double-* ambiguous language
    val tests: Set[GrammarTestCases] = Set(
        LookaheadIsGrammar1,
        LookaheadIsGrammar2)
}

class LookaheadIsTestSuite1 extends BasicParseTest(GrammarWithLookaheadIs.tests)
