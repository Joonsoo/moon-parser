package com.giyeok.moonparser.tests

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SampleGrammar1Tests extends FunSuite {
    import scala.collection.immutable.ListMap
    import com.giyeok.moonparser.Grammar
    import com.giyeok.moonparser.GrElems._
    import com.giyeok.moonparser.ParsedSymbols._
    import com.giyeok.moonparser.InputPieces._
    import com.giyeok.moonparser.dynamic.Parser
    import com.giyeok.moonparser.dynamic.Parser.Result._
    import com.giyeok.moonparser.dynamic.BasicBlackboxParser

    val grammar1 = new BasicBlackboxParser(new Grammar {
        // grammar only with StringInput, Nonterminal, Sequence
        val name = "Basic Grammar"
        val startSymbol: String = "S"
        val rules: RuleMap = ListMap(
            "S" -> Set(seq(n("A"), n("B"))),
            "A" -> Set(i("a"), seq(i("a"), n("A"))),
            "B" -> Set(seq(i("b"), n("B")), e))
    })

    test("Basic Grammar - test 1: aaa") {
        val t1 = grammar1.parse("aaa")
        assert(t1.parsedOpt match {
            case Some(NontermSymbol(Sequence(Seq(Nonterminal("A"), Nonterminal("B")), _: Set[_]), List(
                NontermSymbol(Nonterminal("A"), List(
                    NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                        NontermSymbol(StringInputElem("a"), List(TermSymbol(CharInput('a'), 0))),
                        NontermSymbol(Nonterminal("A"), List(
                            NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                                NontermSymbol(StringInputElem("a"), List(TermSymbol(CharInput('a'), 1))),
                                NontermSymbol(Nonterminal("A"), List(
                                    NontermSymbol(StringInputElem("a"), List(TermSymbol(CharInput('a'), 2))))))))))))),
                EmptySymbol(Nonterminal("B"))))) => true
            case _ => false
        })
    }

    test("Basic Grammar - test 2: abbb") {
        val t2 = grammar1.parse("abbb")
        assert(t2.parsedOpt match {
            case Some(NontermSymbol(Sequence(Seq(Nonterminal("A"), Nonterminal("B")), _: Set[_]), List(
                NontermSymbol(Nonterminal("A"), List(
                    NontermSymbol(StringInputElem("a"), List(
                        TermSymbol(CharInput('a'), 0))))),
                NontermSymbol(Nonterminal("B"), List(
                    NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                        NontermSymbol(StringInputElem("b"), List(
                            TermSymbol(CharInput('b'), 1))), NontermSymbol(Nonterminal("B"), List(
                            NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                NontermSymbol(StringInputElem("b"), List(
                                    TermSymbol(CharInput('b'), 2))), NontermSymbol(Nonterminal("B"), List(
                                    NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                        NontermSymbol(StringInputElem("b"), List(
                                            TermSymbol(CharInput('b'), 3))), EmptySymbol(Nonterminal("B"))))))))))))))))) => true
            case _ => false
        })
    }

    test("Basic Grammar - test 3: aaaaabbbbb") {
        val t3 = grammar1.parse("aaaaabbbbb")
        assert(t3.parsedOpt match {
            case Some(NontermSymbol(Sequence(Seq(Nonterminal("A"), Nonterminal("B")), _: Set[_]), List(
                NontermSymbol(Nonterminal("A"), List(
                    NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                        NontermSymbol(StringInputElem("a"), List(
                            TermSymbol(CharInput('a'), 0))), NontermSymbol(Nonterminal("A"), List(
                            NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                                NontermSymbol(StringInputElem("a"), List(
                                    TermSymbol(CharInput('a'), 1))), NontermSymbol(Nonterminal("A"), List(
                                    NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                                        NontermSymbol(StringInputElem("a"), List(
                                            TermSymbol(CharInput('a'), 2))), NontermSymbol(Nonterminal("A"), List(
                                            NontermSymbol(Sequence(Seq(StringInputElem("a"), Nonterminal("A")), _: Set[_]), List(
                                                NontermSymbol(StringInputElem("a"), List(
                                                    TermSymbol(CharInput('a'), 3))), NontermSymbol(Nonterminal("A"), List(
                                                    NontermSymbol(StringInputElem("a"), List(TermSymbol(CharInput('a'), 4))))))))))))))))))))),
                NontermSymbol(Nonterminal("B"), List(
                    NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                        NontermSymbol(StringInputElem("b"), List(
                            TermSymbol(CharInput('b'), 5))), NontermSymbol(Nonterminal("B"), List(
                            NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                NontermSymbol(StringInputElem("b"), List(
                                    TermSymbol(CharInput('b'), 6))), NontermSymbol(Nonterminal("B"), List(
                                    NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                        NontermSymbol(StringInputElem("b"), List(
                                            TermSymbol(CharInput('b'), 7))), NontermSymbol(Nonterminal("B"), List(
                                            NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                                NontermSymbol(StringInputElem("b"), List(
                                                    TermSymbol(CharInput('b'), 8))), NontermSymbol(Nonterminal("B"), List(
                                                    NontermSymbol(Sequence(Seq(StringInputElem("b"), Nonterminal("B")), _: Set[_]), List(
                                                        NontermSymbol(StringInputElem("b"), List(
                                                            TermSymbol(CharInput('b'), 9))), EmptySymbol(Nonterminal("B"))))))))))))))))))))))))) => true
            case _ => false
        })
    }
    test("Basic Grammar - test 4: long text") {
        val longText = "a" * 500
        val t4 = grammar1.parse(longText)
        assert(t4.parsedOpt match {
            case Some(cs: ConcreteSymbol) if cs.text == longText => true
            case _ => false
        })
    }
    // t5 = parser.parse("c")  // test it after implement backup items support
}