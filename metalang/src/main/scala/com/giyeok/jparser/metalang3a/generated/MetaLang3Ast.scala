package com.giyeok.jparser.metalang3a.generated

import com.giyeok.jparser.Inputs
import com.giyeok.jparser.NGrammar
import com.giyeok.jparser.ParseResultTree.BindNode
import com.giyeok.jparser.ParseResultTree.Node
import com.giyeok.jparser.ParseResultTree.SequenceNode
import com.giyeok.jparser.ParseResultTree.TerminalNode
import com.giyeok.jparser.ParsingErrors
import com.giyeok.jparser.Symbols
import com.giyeok.jparser.nparser.NaiveParser
import com.giyeok.jparser.nparser.ParseTreeUtil
import com.giyeok.jparser.nparser.ParseTreeUtil.unrollRepeat0
import com.giyeok.jparser.nparser.ParseTreeUtil.unrollRepeat1
import com.giyeok.jparser.nparser.Parser
import scala.collection.immutable.ListSet

object MetaLang3Ast {
  val ngrammar = new NGrammar(
    Map(1 -> NGrammar.NStart(1, 2),
      2 -> NGrammar.NNonterminal(2, Symbols.Nonterminal("Grammar"), Set(3)),
      4 -> NGrammar.NNonterminal(4, Symbols.Nonterminal("WS"), Set(5)),
      6 -> NGrammar.NRepeat(6, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), 7, 8),
      9 -> NGrammar.NOneOf(9, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), Set(10, 13)),
      10 -> NGrammar.NProxy(10, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), 11),
      12 -> NGrammar.NTerminal(12, Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)),
      13 -> NGrammar.NProxy(13, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))), 14),
      15 -> NGrammar.NNonterminal(15, Symbols.Nonterminal("LineComment"), Set(16)),
      17 -> NGrammar.NProxy(17, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('/'), Symbols.ExactChar('/')))), 18),
      19 -> NGrammar.NTerminal(19, Symbols.ExactChar('/')),
      20 -> NGrammar.NRepeat(20, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))))), 0), 7, 21),
      22 -> NGrammar.NOneOf(22, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))))), Set(23)),
      23 -> NGrammar.NProxy(23, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))), 24),
      25 -> NGrammar.NExcept(25, Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n')), 26, 27),
      26 -> NGrammar.NTerminal(26, Symbols.AnyChar),
      27 -> NGrammar.NTerminal(27, Symbols.ExactChar('\n')),
      28 -> NGrammar.NOneOf(28, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("EOF")))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('\n')))))), Set(29, 34)),
      29 -> NGrammar.NProxy(29, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("EOF")))), 30),
      31 -> NGrammar.NNonterminal(31, Symbols.Nonterminal("EOF"), Set(32)),
      33 -> NGrammar.NLookaheadExcept(33, Symbols.LookaheadExcept(Symbols.AnyChar), 7, 26),
      34 -> NGrammar.NProxy(34, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('\n')))), 35),
      36 -> NGrammar.NNonterminal(36, Symbols.Nonterminal("Def"), Set(37, 120)),
      38 -> NGrammar.NNonterminal(38, Symbols.Nonterminal("Rule"), Set(39)),
      40 -> NGrammar.NNonterminal(40, Symbols.Nonterminal("LHS"), Set(41)),
      42 -> NGrammar.NNonterminal(42, Symbols.Nonterminal("Nonterminal"), Set(43)),
      44 -> NGrammar.NNonterminal(44, Symbols.Nonterminal("NonterminalName"), Set(45, 93)),
      46 -> NGrammar.NNonterminal(46, Symbols.Nonterminal("IdNoKeyword"), Set(47)),
      48 -> NGrammar.NExcept(48, Symbols.Except(Symbols.Nonterminal("Id"), Symbols.Nonterminal("Keyword")), 49, 59),
      49 -> NGrammar.NNonterminal(49, Symbols.Nonterminal("Id"), Set(50)),
      51 -> NGrammar.NLongest(51, Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0))))))), 52),
      52 -> NGrammar.NOneOf(52, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0)))))), Set(53)),
      53 -> NGrammar.NProxy(53, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0)))), 54),
      55 -> NGrammar.NTerminal(55, Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet)),
      56 -> NGrammar.NRepeat(56, Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0), 7, 57),
      58 -> NGrammar.NTerminal(58, Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet)),
      59 -> NGrammar.NNonterminal(59, Symbols.Nonterminal("Keyword"), Set(60, 69, 75, 82, 86, 90)),
      61 -> NGrammar.NProxy(61, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('b'), Symbols.ExactChar('o'), Symbols.ExactChar('o'), Symbols.ExactChar('l'), Symbols.ExactChar('e'), Symbols.ExactChar('a'), Symbols.ExactChar('n')))), 62),
      63 -> NGrammar.NTerminal(63, Symbols.ExactChar('b')),
      64 -> NGrammar.NTerminal(64, Symbols.ExactChar('o')),
      65 -> NGrammar.NTerminal(65, Symbols.ExactChar('l')),
      66 -> NGrammar.NTerminal(66, Symbols.ExactChar('e')),
      67 -> NGrammar.NTerminal(67, Symbols.ExactChar('a')),
      68 -> NGrammar.NTerminal(68, Symbols.ExactChar('n')),
      70 -> NGrammar.NProxy(70, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('c'), Symbols.ExactChar('h'), Symbols.ExactChar('a'), Symbols.ExactChar('r')))), 71),
      72 -> NGrammar.NTerminal(72, Symbols.ExactChar('c')),
      73 -> NGrammar.NTerminal(73, Symbols.ExactChar('h')),
      74 -> NGrammar.NTerminal(74, Symbols.ExactChar('r')),
      76 -> NGrammar.NProxy(76, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('s'), Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('i'), Symbols.ExactChar('n'), Symbols.ExactChar('g')))), 77),
      78 -> NGrammar.NTerminal(78, Symbols.ExactChar('s')),
      79 -> NGrammar.NTerminal(79, Symbols.ExactChar('t')),
      80 -> NGrammar.NTerminal(80, Symbols.ExactChar('i')),
      81 -> NGrammar.NTerminal(81, Symbols.ExactChar('g')),
      83 -> NGrammar.NProxy(83, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e')))), 84),
      85 -> NGrammar.NTerminal(85, Symbols.ExactChar('u')),
      87 -> NGrammar.NProxy(87, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e')))), 88),
      89 -> NGrammar.NTerminal(89, Symbols.ExactChar('f')),
      91 -> NGrammar.NProxy(91, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('n'), Symbols.ExactChar('u'), Symbols.ExactChar('l'), Symbols.ExactChar('l')))), 92),
      94 -> NGrammar.NTerminal(94, Symbols.ExactChar('`')),
      95 -> NGrammar.NOneOf(95, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(96, 138)),
      96 -> NGrammar.NOneOf(96, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))))), Set(97)),
      97 -> NGrammar.NProxy(97, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))), 98),
      99 -> NGrammar.NTerminal(99, Symbols.ExactChar(':')),
      100 -> NGrammar.NNonterminal(100, Symbols.Nonterminal("TypeDesc"), Set(101)),
      102 -> NGrammar.NNonterminal(102, Symbols.Nonterminal("NonNullTypeDesc"), Set(103, 105, 108, 110, 116, 120)),
      104 -> NGrammar.NNonterminal(104, Symbols.Nonterminal("TypeName"), Set(45, 93)),
      106 -> NGrammar.NTerminal(106, Symbols.ExactChar('[')),
      107 -> NGrammar.NTerminal(107, Symbols.ExactChar(']')),
      109 -> NGrammar.NNonterminal(109, Symbols.Nonterminal("ValueType"), Set(60, 69, 75)),
      111 -> NGrammar.NNonterminal(111, Symbols.Nonterminal("AnyType"), Set(112)),
      113 -> NGrammar.NProxy(113, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('a'), Symbols.ExactChar('n'), Symbols.ExactChar('y')))), 114),
      115 -> NGrammar.NTerminal(115, Symbols.ExactChar('y')),
      117 -> NGrammar.NNonterminal(117, Symbols.Nonterminal("EnumTypeName"), Set(118)),
      119 -> NGrammar.NTerminal(119, Symbols.ExactChar('%')),
      121 -> NGrammar.NNonterminal(121, Symbols.Nonterminal("TypeDef"), Set(122, 158, 179)),
      123 -> NGrammar.NNonterminal(123, Symbols.Nonterminal("ClassDef"), Set(124, 140, 157)),
      125 -> NGrammar.NNonterminal(125, Symbols.Nonterminal("SuperTypes"), Set(126)),
      127 -> NGrammar.NTerminal(127, Symbols.ExactChar('<')),
      128 -> NGrammar.NOneOf(128, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(129, 138)),
      129 -> NGrammar.NOneOf(129, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.Nonterminal("WS")))))), Set(130)),
      130 -> NGrammar.NProxy(130, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.Nonterminal("WS")))), 131),
      132 -> NGrammar.NRepeat(132, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), 7, 133),
      134 -> NGrammar.NOneOf(134, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), Set(135)),
      135 -> NGrammar.NProxy(135, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))), 136),
      137 -> NGrammar.NTerminal(137, Symbols.ExactChar(',')),
      138 -> NGrammar.NProxy(138, Symbols.Proxy(Symbols.Sequence(Seq())), 7),
      139 -> NGrammar.NTerminal(139, Symbols.ExactChar('>')),
      141 -> NGrammar.NNonterminal(141, Symbols.Nonterminal("ClassParamsDef"), Set(142)),
      143 -> NGrammar.NTerminal(143, Symbols.ExactChar('(')),
      144 -> NGrammar.NOneOf(144, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ClassParamDef"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(145, 138)),
      145 -> NGrammar.NOneOf(145, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ClassParamDef"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.Nonterminal("WS")))))), Set(146)),
      146 -> NGrammar.NProxy(146, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ClassParamDef"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.Nonterminal("WS")))), 147),
      148 -> NGrammar.NNonterminal(148, Symbols.Nonterminal("ClassParamDef"), Set(149)),
      150 -> NGrammar.NNonterminal(150, Symbols.Nonterminal("ParamName"), Set(45, 93)),
      151 -> NGrammar.NRepeat(151, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), 7, 152),
      153 -> NGrammar.NOneOf(153, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), Set(154)),
      154 -> NGrammar.NProxy(154, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))), 155),
      156 -> NGrammar.NTerminal(156, Symbols.ExactChar(')')),
      159 -> NGrammar.NNonterminal(159, Symbols.Nonterminal("SuperDef"), Set(160)),
      161 -> NGrammar.NOneOf(161, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(162, 138)),
      162 -> NGrammar.NOneOf(162, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes")))))), Set(163)),
      163 -> NGrammar.NProxy(163, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes")))), 164),
      165 -> NGrammar.NTerminal(165, Symbols.ExactChar('{')),
      166 -> NGrammar.NOneOf(166, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubTypes")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(167, 138)),
      167 -> NGrammar.NOneOf(167, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubTypes")))))), Set(168)),
      168 -> NGrammar.NProxy(168, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubTypes")))), 169),
      170 -> NGrammar.NNonterminal(170, Symbols.Nonterminal("SubTypes"), Set(171)),
      172 -> NGrammar.NNonterminal(172, Symbols.Nonterminal("SubType"), Set(103, 122, 158)),
      173 -> NGrammar.NRepeat(173, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))))), 0), 7, 174),
      175 -> NGrammar.NOneOf(175, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))))), Set(176)),
      176 -> NGrammar.NProxy(176, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))), 177),
      178 -> NGrammar.NTerminal(178, Symbols.ExactChar('}')),
      180 -> NGrammar.NNonterminal(180, Symbols.Nonterminal("EnumTypeDef"), Set(181)),
      182 -> NGrammar.NOneOf(182, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("Id"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0)))))), Set(183)),
      183 -> NGrammar.NProxy(183, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("Id"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0)))), 184),
      185 -> NGrammar.NRepeat(185, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0), 7, 186),
      187 -> NGrammar.NOneOf(187, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), Set(188)),
      188 -> NGrammar.NProxy(188, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))), 189),
      190 -> NGrammar.NOneOf(190, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('?')))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(191, 138)),
      191 -> NGrammar.NOneOf(191, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('?')))))), Set(192)),
      192 -> NGrammar.NProxy(192, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('?')))), 193),
      194 -> NGrammar.NTerminal(194, Symbols.ExactChar('?')),
      195 -> NGrammar.NTerminal(195, Symbols.ExactChar('=')),
      196 -> NGrammar.NOneOf(196, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("RHS"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0)))))), Set(197)),
      197 -> NGrammar.NProxy(197, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("RHS"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0)))), 198),
      199 -> NGrammar.NNonterminal(199, Symbols.Nonterminal("RHS"), Set(200)),
      201 -> NGrammar.NNonterminal(201, Symbols.Nonterminal("Sequence"), Set(202)),
      203 -> NGrammar.NNonterminal(203, Symbols.Nonterminal("Elem"), Set(204, 289)),
      205 -> NGrammar.NNonterminal(205, Symbols.Nonterminal("Symbol"), Set(206)),
      207 -> NGrammar.NNonterminal(207, Symbols.Nonterminal("BinSymbol"), Set(208, 287, 288)),
      209 -> NGrammar.NTerminal(209, Symbols.ExactChar('&')),
      210 -> NGrammar.NNonterminal(210, Symbols.Nonterminal("PreUnSymbol"), Set(211, 213, 215)),
      212 -> NGrammar.NTerminal(212, Symbols.ExactChar('^')),
      214 -> NGrammar.NTerminal(214, Symbols.ExactChar('!')),
      216 -> NGrammar.NNonterminal(216, Symbols.Nonterminal("PostUnSymbol"), Set(217, 218, 220, 222)),
      219 -> NGrammar.NTerminal(219, Symbols.ExactChar('*')),
      221 -> NGrammar.NTerminal(221, Symbols.ExactChar('+')),
      223 -> NGrammar.NNonterminal(223, Symbols.Nonterminal("AtomSymbol"), Set(224, 240, 258, 270, 271, 280, 283)),
      225 -> NGrammar.NNonterminal(225, Symbols.Nonterminal("Terminal"), Set(226, 238)),
      227 -> NGrammar.NTerminal(227, Symbols.ExactChar('\'')),
      228 -> NGrammar.NNonterminal(228, Symbols.Nonterminal("TerminalChar"), Set(229, 232, 234)),
      230 -> NGrammar.NExcept(230, Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\\')), 26, 231),
      231 -> NGrammar.NTerminal(231, Symbols.ExactChar('\\')),
      233 -> NGrammar.NTerminal(233, Symbols.Chars(Set('\'', '\\', 'b', 'n', 'r', 't'))),
      235 -> NGrammar.NNonterminal(235, Symbols.Nonterminal("UnicodeChar"), Set(236)),
      237 -> NGrammar.NTerminal(237, Symbols.Chars(('0' to '9').toSet ++ ('A' to 'F').toSet ++ ('a' to 'f').toSet)),
      239 -> NGrammar.NTerminal(239, Symbols.ExactChar('.')),
      241 -> NGrammar.NNonterminal(241, Symbols.Nonterminal("TerminalChoice"), Set(242, 257)),
      243 -> NGrammar.NNonterminal(243, Symbols.Nonterminal("TerminalChoiceElem"), Set(244, 251)),
      245 -> NGrammar.NNonterminal(245, Symbols.Nonterminal("TerminalChoiceChar"), Set(246, 249, 234)),
      247 -> NGrammar.NExcept(247, Symbols.Except(Symbols.AnyChar, Symbols.Chars(Set('\'', '-', '\\'))), 26, 248),
      248 -> NGrammar.NTerminal(248, Symbols.Chars(Set('\'', '-', '\\'))),
      250 -> NGrammar.NTerminal(250, Symbols.Chars(Set('\'', '-', '\\', 'b', 'n', 'r', 't'))),
      252 -> NGrammar.NNonterminal(252, Symbols.Nonterminal("TerminalChoiceRange"), Set(253)),
      254 -> NGrammar.NTerminal(254, Symbols.ExactChar('-')),
      255 -> NGrammar.NRepeat(255, Symbols.Repeat(Symbols.Nonterminal("TerminalChoiceElem"), 1), 243, 256),
      259 -> NGrammar.NNonterminal(259, Symbols.Nonterminal("StringSymbol"), Set(260)),
      261 -> NGrammar.NTerminal(261, Symbols.ExactChar('"')),
      262 -> NGrammar.NRepeat(262, Symbols.Repeat(Symbols.Nonterminal("StringChar"), 0), 7, 263),
      264 -> NGrammar.NNonterminal(264, Symbols.Nonterminal("StringChar"), Set(265, 268, 234)),
      266 -> NGrammar.NExcept(266, Symbols.Except(Symbols.AnyChar, Symbols.Chars(Set('"', '\\'))), 26, 267),
      267 -> NGrammar.NTerminal(267, Symbols.Chars(Set('"', '\\'))),
      269 -> NGrammar.NTerminal(269, Symbols.Chars(Set('"', '\\', 'b', 'n', 'r', 't'))),
      272 -> NGrammar.NNonterminal(272, Symbols.Nonterminal("InPlaceChoices"), Set(273)),
      274 -> NGrammar.NRepeat(274, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))))), 0), 7, 275),
      276 -> NGrammar.NOneOf(276, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))))), Set(277)),
      277 -> NGrammar.NProxy(277, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))), 278),
      279 -> NGrammar.NTerminal(279, Symbols.ExactChar('|')),
      281 -> NGrammar.NNonterminal(281, Symbols.Nonterminal("Longest"), Set(282)),
      284 -> NGrammar.NNonterminal(284, Symbols.Nonterminal("EmptySequence"), Set(285)),
      286 -> NGrammar.NTerminal(286, Symbols.ExactChar('#')),
      290 -> NGrammar.NNonterminal(290, Symbols.Nonterminal("Processor"), Set(291, 325)),
      292 -> NGrammar.NNonterminal(292, Symbols.Nonterminal("Ref"), Set(293, 320)),
      294 -> NGrammar.NNonterminal(294, Symbols.Nonterminal("ValRef"), Set(295)),
      296 -> NGrammar.NTerminal(296, Symbols.ExactChar('$')),
      297 -> NGrammar.NOneOf(297, Symbols.OneOf(ListSet(Symbols.Nonterminal("CondSymPath"), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(298, 138)),
      298 -> NGrammar.NNonterminal(298, Symbols.Nonterminal("CondSymPath"), Set(299)),
      300 -> NGrammar.NRepeat(300, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))))), 1), 301, 306),
      301 -> NGrammar.NOneOf(301, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))))), Set(302, 304)),
      302 -> NGrammar.NProxy(302, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), 303),
      304 -> NGrammar.NProxy(304, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))), 305),
      307 -> NGrammar.NNonterminal(307, Symbols.Nonterminal("RefIdx"), Set(308)),
      309 -> NGrammar.NLongest(309, Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('0')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(('1' to '9').toSet), Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0))))))), 310),
      310 -> NGrammar.NOneOf(310, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('0')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(('1' to '9').toSet), Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0)))))), Set(311, 314)),
      311 -> NGrammar.NProxy(311, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('0')))), 312),
      313 -> NGrammar.NTerminal(313, Symbols.ExactChar('0')),
      314 -> NGrammar.NProxy(314, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(('1' to '9').toSet), Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0)))), 315),
      316 -> NGrammar.NTerminal(316, Symbols.Chars(('1' to '9').toSet)),
      317 -> NGrammar.NRepeat(317, Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0), 7, 318),
      319 -> NGrammar.NTerminal(319, Symbols.Chars(('0' to '9').toSet)),
      321 -> NGrammar.NNonterminal(321, Symbols.Nonterminal("RawRef"), Set(322)),
      323 -> NGrammar.NProxy(323, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.ExactChar('$')))), 324),
      326 -> NGrammar.NNonterminal(326, Symbols.Nonterminal("PExprBlock"), Set(327)),
      328 -> NGrammar.NNonterminal(328, Symbols.Nonterminal("PExpr"), Set(329, 439)),
      330 -> NGrammar.NNonterminal(330, Symbols.Nonterminal("TernaryExpr"), Set(331, 440)),
      332 -> NGrammar.NNonterminal(332, Symbols.Nonterminal("BoolOrExpr"), Set(333, 435)),
      334 -> NGrammar.NNonterminal(334, Symbols.Nonterminal("BoolAndExpr"), Set(335, 432)),
      336 -> NGrammar.NNonterminal(336, Symbols.Nonterminal("BoolEqExpr"), Set(337, 429)),
      338 -> NGrammar.NNonterminal(338, Symbols.Nonterminal("ElvisExpr"), Set(339, 419)),
      340 -> NGrammar.NNonterminal(340, Symbols.Nonterminal("AdditiveExpr"), Set(341, 416)),
      342 -> NGrammar.NNonterminal(342, Symbols.Nonterminal("PrefixNotExpr"), Set(343, 344)),
      345 -> NGrammar.NNonterminal(345, Symbols.Nonterminal("Atom"), Set(291, 346, 350, 365, 380, 383, 397, 412)),
      347 -> NGrammar.NNonterminal(347, Symbols.Nonterminal("BindExpr"), Set(348)),
      349 -> NGrammar.NNonterminal(349, Symbols.Nonterminal("BinderExpr"), Set(291, 346, 325)),
      351 -> NGrammar.NNonterminal(351, Symbols.Nonterminal("NamedConstructExpr"), Set(352)),
      353 -> NGrammar.NNonterminal(353, Symbols.Nonterminal("NamedConstructParams"), Set(354)),
      355 -> NGrammar.NOneOf(355, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("NamedParam"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), Symbols.Nonterminal("WS")))))), Set(356)),
      356 -> NGrammar.NProxy(356, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("NamedParam"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), Symbols.Nonterminal("WS")))), 357),
      358 -> NGrammar.NNonterminal(358, Symbols.Nonterminal("NamedParam"), Set(359)),
      360 -> NGrammar.NRepeat(360, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), 7, 361),
      362 -> NGrammar.NOneOf(362, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), Set(363)),
      363 -> NGrammar.NProxy(363, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))), 364),
      366 -> NGrammar.NNonterminal(366, Symbols.Nonterminal("FuncCallOrConstructExpr"), Set(367)),
      368 -> NGrammar.NNonterminal(368, Symbols.Nonterminal("TypeOrFuncName"), Set(45, 93)),
      369 -> NGrammar.NNonterminal(369, Symbols.Nonterminal("CallParams"), Set(370)),
      371 -> NGrammar.NOneOf(371, Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Set(372, 138)),
      372 -> NGrammar.NOneOf(372, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS")))))), Set(373)),
      373 -> NGrammar.NProxy(373, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS")))), 374),
      375 -> NGrammar.NRepeat(375, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), 7, 376),
      377 -> NGrammar.NOneOf(377, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), Set(378)),
      378 -> NGrammar.NProxy(378, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))), 379),
      381 -> NGrammar.NNonterminal(381, Symbols.Nonterminal("ArrayExpr"), Set(382)),
      384 -> NGrammar.NNonterminal(384, Symbols.Nonterminal("Literal"), Set(90, 385, 389, 392)),
      386 -> NGrammar.NOneOf(386, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e'))))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e'))))))))), Set(387, 388)),
      387 -> NGrammar.NProxy(387, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e'))))))), 82),
      388 -> NGrammar.NProxy(388, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e'))))))), 86),
      390 -> NGrammar.NNonterminal(390, Symbols.Nonterminal("CharChar"), Set(391)),
      393 -> NGrammar.NRepeat(393, Symbols.Repeat(Symbols.Nonterminal("StrChar"), 0), 7, 394),
      395 -> NGrammar.NNonterminal(395, Symbols.Nonterminal("StrChar"), Set(396)),
      398 -> NGrammar.NNonterminal(398, Symbols.Nonterminal("EnumValue"), Set(399)),
      400 -> NGrammar.NLongest(400, Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("CanonicalEnumValue")))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ShortenedEnumValue"))))))), 401),
      401 -> NGrammar.NOneOf(401, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("CanonicalEnumValue")))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ShortenedEnumValue")))))), Set(402, 408)),
      402 -> NGrammar.NProxy(402, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("CanonicalEnumValue")))), 403),
      404 -> NGrammar.NNonterminal(404, Symbols.Nonterminal("CanonicalEnumValue"), Set(405)),
      406 -> NGrammar.NNonterminal(406, Symbols.Nonterminal("EnumValueName"), Set(407)),
      408 -> NGrammar.NProxy(408, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ShortenedEnumValue")))), 409),
      410 -> NGrammar.NNonterminal(410, Symbols.Nonterminal("ShortenedEnumValue"), Set(411)),
      413 -> NGrammar.NOneOf(413, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('+')))))), Set(414)),
      414 -> NGrammar.NProxy(414, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('+')))), 415),
      417 -> NGrammar.NProxy(417, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('?'), Symbols.ExactChar(':')))), 418),
      420 -> NGrammar.NOneOf(420, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('='))))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('='))))))))), Set(421, 425)),
      421 -> NGrammar.NProxy(421, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('='))))))), 422),
      423 -> NGrammar.NProxy(423, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('=')))), 424),
      425 -> NGrammar.NProxy(425, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('='))))))), 426),
      427 -> NGrammar.NProxy(427, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('=')))), 428),
      430 -> NGrammar.NProxy(430, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('|'), Symbols.ExactChar('|')))), 431),
      433 -> NGrammar.NProxy(433, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('&'), Symbols.ExactChar('&')))), 434),
      436 -> NGrammar.NLongest(436, Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr"))))))), 437),
      437 -> NGrammar.NOneOf(437, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr")))))), Set(438)),
      438 -> NGrammar.NProxy(438, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr")))), 439),
      441 -> NGrammar.NRepeat(441, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))))), 0), 7, 442),
      443 -> NGrammar.NOneOf(443, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))))), Set(444)),
      444 -> NGrammar.NProxy(444, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))), 445),
      446 -> NGrammar.NRepeat(446, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0), 7, 447),
      448 -> NGrammar.NOneOf(448, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), Set(449)),
      449 -> NGrammar.NProxy(449, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))), 450),
      451 -> NGrammar.NRepeat(451, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))))), 0), 7, 452),
      453 -> NGrammar.NOneOf(453, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))))), Set(454)),
      454 -> NGrammar.NProxy(454, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))), 455),
      456 -> NGrammar.NNonterminal(456, Symbols.Nonterminal("WSNL"), Set(457)),
      458 -> NGrammar.NLongest(458, Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.ExactChar('\n'), Symbols.Nonterminal("WS"))))))), 459),
      459 -> NGrammar.NOneOf(459, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.ExactChar('\n'), Symbols.Nonterminal("WS")))))), Set(460)),
      460 -> NGrammar.NProxy(460, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.ExactChar('\n'), Symbols.Nonterminal("WS")))), 461),
      462 -> NGrammar.NRepeat(462, Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), 7, 463),
      464 -> NGrammar.NOneOf(464, Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), Set(465, 13)),
      465 -> NGrammar.NProxy(465, Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), 466),
      467 -> NGrammar.NTerminal(467, Symbols.Chars(Set('\t', '\r', ' ')))),
    Map(3 -> NGrammar.NSequence(3, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Def"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))))), 0), Symbols.Nonterminal("WS"))), Seq(4, 36, 451, 4)),
      5 -> NGrammar.NSequence(5, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0))), Seq(6)),
      7 -> NGrammar.NSequence(7, Symbols.Sequence(Seq()), Seq()),
      8 -> NGrammar.NSequence(8, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet)))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))))), Seq(6, 9)),
      11 -> NGrammar.NSequence(11, Symbols.Sequence(Seq(Symbols.Chars(Set('\r', ' ') ++ ('\t' to '\n').toSet))), Seq(12)),
      14 -> NGrammar.NSequence(14, Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment"))), Seq(15)),
      16 -> NGrammar.NSequence(16, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('/'), Symbols.ExactChar('/')))), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("EOF")))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('\n')))))))), Seq(17, 20, 28)),
      18 -> NGrammar.NSequence(18, Symbols.Sequence(Seq(Symbols.ExactChar('/'), Symbols.ExactChar('/'))), Seq(19, 19)),
      21 -> NGrammar.NSequence(21, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n'))))))))), Seq(20, 22)),
      24 -> NGrammar.NSequence(24, Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\n')))), Seq(25)),
      30 -> NGrammar.NSequence(30, Symbols.Sequence(Seq(Symbols.Nonterminal("EOF"))), Seq(31)),
      32 -> NGrammar.NSequence(32, Symbols.Sequence(Seq(Symbols.LookaheadExcept(Symbols.AnyChar))), Seq(33)),
      35 -> NGrammar.NSequence(35, Symbols.Sequence(Seq(Symbols.ExactChar('\n'))), Seq(27)),
      37 -> NGrammar.NSequence(37, Symbols.Sequence(Seq(Symbols.Nonterminal("Rule"))), Seq(38)),
      39 -> NGrammar.NSequence(39, Symbols.Sequence(Seq(Symbols.Nonterminal("LHS"), Symbols.Nonterminal("WS"), Symbols.ExactChar('='), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("RHS"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0)))))))), Seq(40, 4, 195, 4, 196)),
      41 -> NGrammar.NSequence(41, Symbols.Sequence(Seq(Symbols.Nonterminal("Nonterminal"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))))), Symbols.Proxy(Symbols.Sequence(Seq())))))), Seq(42, 95)),
      43 -> NGrammar.NSequence(43, Symbols.Sequence(Seq(Symbols.Nonterminal("NonterminalName"))), Seq(44)),
      45 -> NGrammar.NSequence(45, Symbols.Sequence(Seq(Symbols.Nonterminal("IdNoKeyword"))), Seq(46)),
      47 -> NGrammar.NSequence(47, Symbols.Sequence(Seq(Symbols.Except(Symbols.Nonterminal("Id"), Symbols.Nonterminal("Keyword")))), Seq(48)),
      50 -> NGrammar.NSequence(50, Symbols.Sequence(Seq(Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0))))))))), Seq(51)),
      54 -> NGrammar.NSequence(54, Symbols.Sequence(Seq(Symbols.Chars(Set('_') ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0))), Seq(55, 56)),
      57 -> NGrammar.NSequence(57, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet), 0), Symbols.Chars(Set('_') ++ ('0' to '9').toSet ++ ('A' to 'Z').toSet ++ ('a' to 'z').toSet))), Seq(56, 58)),
      60 -> NGrammar.NSequence(60, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('b'), Symbols.ExactChar('o'), Symbols.ExactChar('o'), Symbols.ExactChar('l'), Symbols.ExactChar('e'), Symbols.ExactChar('a'), Symbols.ExactChar('n')))))), Seq(61)),
      62 -> NGrammar.NSequence(62, Symbols.Sequence(Seq(Symbols.ExactChar('b'), Symbols.ExactChar('o'), Symbols.ExactChar('o'), Symbols.ExactChar('l'), Symbols.ExactChar('e'), Symbols.ExactChar('a'), Symbols.ExactChar('n'))), Seq(63, 64, 64, 65, 66, 67, 68)),
      69 -> NGrammar.NSequence(69, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('c'), Symbols.ExactChar('h'), Symbols.ExactChar('a'), Symbols.ExactChar('r')))))), Seq(70)),
      71 -> NGrammar.NSequence(71, Symbols.Sequence(Seq(Symbols.ExactChar('c'), Symbols.ExactChar('h'), Symbols.ExactChar('a'), Symbols.ExactChar('r'))), Seq(72, 73, 67, 74)),
      75 -> NGrammar.NSequence(75, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('s'), Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('i'), Symbols.ExactChar('n'), Symbols.ExactChar('g')))))), Seq(76)),
      77 -> NGrammar.NSequence(77, Symbols.Sequence(Seq(Symbols.ExactChar('s'), Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('i'), Symbols.ExactChar('n'), Symbols.ExactChar('g'))), Seq(78, 79, 74, 80, 68, 81)),
      82 -> NGrammar.NSequence(82, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e')))))), Seq(83)),
      84 -> NGrammar.NSequence(84, Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e'))), Seq(79, 74, 85, 66)),
      86 -> NGrammar.NSequence(86, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e')))))), Seq(87)),
      88 -> NGrammar.NSequence(88, Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e'))), Seq(89, 67, 65, 78, 66)),
      90 -> NGrammar.NSequence(90, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('n'), Symbols.ExactChar('u'), Symbols.ExactChar('l'), Symbols.ExactChar('l')))))), Seq(91)),
      92 -> NGrammar.NSequence(92, Symbols.Sequence(Seq(Symbols.ExactChar('n'), Symbols.ExactChar('u'), Symbols.ExactChar('l'), Symbols.ExactChar('l'))), Seq(68, 85, 65, 65)),
      93 -> NGrammar.NSequence(93, Symbols.Sequence(Seq(Symbols.ExactChar('`'), Symbols.Nonterminal("Id"), Symbols.ExactChar('`'))), Seq(94, 49, 94)),
      98 -> NGrammar.NSequence(98, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc"))), Seq(4, 99, 4, 100)),
      101 -> NGrammar.NSequence(101, Symbols.Sequence(Seq(Symbols.Nonterminal("NonNullTypeDesc"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('?')))))), Symbols.Proxy(Symbols.Sequence(Seq())))))), Seq(102, 190)),
      103 -> NGrammar.NSequence(103, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"))), Seq(104)),
      105 -> NGrammar.NSequence(105, Symbols.Sequence(Seq(Symbols.ExactChar('['), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc"), Symbols.Nonterminal("WS"), Symbols.ExactChar(']'))), Seq(106, 4, 100, 4, 107)),
      108 -> NGrammar.NSequence(108, Symbols.Sequence(Seq(Symbols.Nonterminal("ValueType"))), Seq(109)),
      110 -> NGrammar.NSequence(110, Symbols.Sequence(Seq(Symbols.Nonterminal("AnyType"))), Seq(111)),
      112 -> NGrammar.NSequence(112, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('a'), Symbols.ExactChar('n'), Symbols.ExactChar('y')))))), Seq(113)),
      114 -> NGrammar.NSequence(114, Symbols.Sequence(Seq(Symbols.ExactChar('a'), Symbols.ExactChar('n'), Symbols.ExactChar('y'))), Seq(67, 68, 115)),
      116 -> NGrammar.NSequence(116, Symbols.Sequence(Seq(Symbols.Nonterminal("EnumTypeName"))), Seq(117)),
      118 -> NGrammar.NSequence(118, Symbols.Sequence(Seq(Symbols.ExactChar('%'), Symbols.Nonterminal("Id"))), Seq(119, 49)),
      120 -> NGrammar.NSequence(120, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeDef"))), Seq(121)),
      122 -> NGrammar.NSequence(122, Symbols.Sequence(Seq(Symbols.Nonterminal("ClassDef"))), Seq(123)),
      124 -> NGrammar.NSequence(124, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes"))), Seq(104, 4, 125)),
      126 -> NGrammar.NSequence(126, Symbols.Sequence(Seq(Symbols.ExactChar('<'), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.ExactChar('>'))), Seq(127, 4, 128, 139)),
      131 -> NGrammar.NSequence(131, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.Nonterminal("WS"))), Seq(104, 132, 4)),
      133 -> NGrammar.NSequence(133, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName")))))))), Seq(132, 134)),
      136 -> NGrammar.NSequence(136, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeName"))), Seq(4, 137, 4, 104)),
      140 -> NGrammar.NSequence(140, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamsDef"))), Seq(104, 4, 141)),
      142 -> NGrammar.NSequence(142, Symbols.Sequence(Seq(Symbols.ExactChar('('), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ClassParamDef"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("WS"), Symbols.ExactChar(')'))), Seq(143, 4, 144, 4, 156)),
      147 -> NGrammar.NSequence(147, Symbols.Sequence(Seq(Symbols.Nonterminal("ClassParamDef"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.Nonterminal("WS"))), Seq(148, 151, 4)),
      149 -> NGrammar.NSequence(149, Symbols.Sequence(Seq(Symbols.Nonterminal("ParamName"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))))), Symbols.Proxy(Symbols.Sequence(Seq())))))), Seq(150, 95)),
      152 -> NGrammar.NSequence(152, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef")))))))), Seq(151, 153)),
      155 -> NGrammar.NSequence(155, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamDef"))), Seq(4, 137, 4, 148)),
      157 -> NGrammar.NSequence(157, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes"), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ClassParamsDef"))), Seq(104, 4, 125, 4, 141)),
      158 -> NGrammar.NSequence(158, Symbols.Sequence(Seq(Symbols.Nonterminal("SuperDef"))), Seq(159)),
      160 -> NGrammar.NSequence(160, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("WS"), Symbols.ExactChar('{'), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubTypes")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("WS"), Symbols.ExactChar('}'))), Seq(104, 161, 4, 165, 166, 4, 178)),
      164 -> NGrammar.NSequence(164, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes"))), Seq(4, 125)),
      169 -> NGrammar.NSequence(169, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubTypes"))), Seq(4, 170)),
      171 -> NGrammar.NSequence(171, Symbols.Sequence(Seq(Symbols.Nonterminal("SubType"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))))), 0))), Seq(172, 173)),
      174 -> NGrammar.NSequence(174, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType")))))))), Seq(173, 175)),
      177 -> NGrammar.NSequence(177, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("SubType"))), Seq(4, 137, 4, 172)),
      179 -> NGrammar.NSequence(179, Symbols.Sequence(Seq(Symbols.Nonterminal("EnumTypeDef"))), Seq(180)),
      181 -> NGrammar.NSequence(181, Symbols.Sequence(Seq(Symbols.Nonterminal("EnumTypeName"), Symbols.Nonterminal("WS"), Symbols.ExactChar('{'), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("Id"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0)))))), Symbols.Nonterminal("WS"), Symbols.ExactChar('}'))), Seq(117, 4, 165, 4, 182, 4, 178)),
      184 -> NGrammar.NSequence(184, Symbols.Sequence(Seq(Symbols.Nonterminal("Id"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0))), Seq(49, 185)),
      186 -> NGrammar.NSequence(186, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id")))))))), Seq(185, 187)),
      189 -> NGrammar.NSequence(189, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Id"))), Seq(4, 137, 4, 49)),
      193 -> NGrammar.NSequence(193, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('?'))), Seq(4, 194)),
      198 -> NGrammar.NSequence(198, Symbols.Sequence(Seq(Symbols.Nonterminal("RHS"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0))), Seq(199, 446)),
      200 -> NGrammar.NSequence(200, Symbols.Sequence(Seq(Symbols.Nonterminal("Sequence"))), Seq(201)),
      202 -> NGrammar.NSequence(202, Symbols.Sequence(Seq(Symbols.Nonterminal("Elem"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))))), 0))), Seq(203, 441)),
      204 -> NGrammar.NSequence(204, Symbols.Sequence(Seq(Symbols.Nonterminal("Symbol"))), Seq(205)),
      206 -> NGrammar.NSequence(206, Symbols.Sequence(Seq(Symbols.Nonterminal("BinSymbol"))), Seq(207)),
      208 -> NGrammar.NSequence(208, Symbols.Sequence(Seq(Symbols.Nonterminal("BinSymbol"), Symbols.Nonterminal("WS"), Symbols.ExactChar('&'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PreUnSymbol"))), Seq(207, 4, 209, 4, 210)),
      211 -> NGrammar.NSequence(211, Symbols.Sequence(Seq(Symbols.ExactChar('^'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PreUnSymbol"))), Seq(212, 4, 210)),
      213 -> NGrammar.NSequence(213, Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PreUnSymbol"))), Seq(214, 4, 210)),
      215 -> NGrammar.NSequence(215, Symbols.Sequence(Seq(Symbols.Nonterminal("PostUnSymbol"))), Seq(216)),
      217 -> NGrammar.NSequence(217, Symbols.Sequence(Seq(Symbols.Nonterminal("PostUnSymbol"), Symbols.Nonterminal("WS"), Symbols.ExactChar('?'))), Seq(216, 4, 194)),
      218 -> NGrammar.NSequence(218, Symbols.Sequence(Seq(Symbols.Nonterminal("PostUnSymbol"), Symbols.Nonterminal("WS"), Symbols.ExactChar('*'))), Seq(216, 4, 219)),
      220 -> NGrammar.NSequence(220, Symbols.Sequence(Seq(Symbols.Nonterminal("PostUnSymbol"), Symbols.Nonterminal("WS"), Symbols.ExactChar('+'))), Seq(216, 4, 221)),
      222 -> NGrammar.NSequence(222, Symbols.Sequence(Seq(Symbols.Nonterminal("AtomSymbol"))), Seq(223)),
      224 -> NGrammar.NSequence(224, Symbols.Sequence(Seq(Symbols.Nonterminal("Terminal"))), Seq(225)),
      226 -> NGrammar.NSequence(226, Symbols.Sequence(Seq(Symbols.ExactChar('\''), Symbols.Nonterminal("TerminalChar"), Symbols.ExactChar('\''))), Seq(227, 228, 227)),
      229 -> NGrammar.NSequence(229, Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.ExactChar('\\')))), Seq(230)),
      232 -> NGrammar.NSequence(232, Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.Chars(Set('\'', '\\', 'b', 'n', 'r', 't')))), Seq(231, 233)),
      234 -> NGrammar.NSequence(234, Symbols.Sequence(Seq(Symbols.Nonterminal("UnicodeChar"))), Seq(235)),
      236 -> NGrammar.NSequence(236, Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.ExactChar('u'), Symbols.Chars(('0' to '9').toSet ++ ('A' to 'F').toSet ++ ('a' to 'f').toSet), Symbols.Chars(('0' to '9').toSet ++ ('A' to 'F').toSet ++ ('a' to 'f').toSet), Symbols.Chars(('0' to '9').toSet ++ ('A' to 'F').toSet ++ ('a' to 'f').toSet), Symbols.Chars(('0' to '9').toSet ++ ('A' to 'F').toSet ++ ('a' to 'f').toSet))), Seq(231, 85, 237, 237, 237, 237)),
      238 -> NGrammar.NSequence(238, Symbols.Sequence(Seq(Symbols.ExactChar('.'))), Seq(239)),
      240 -> NGrammar.NSequence(240, Symbols.Sequence(Seq(Symbols.Nonterminal("TerminalChoice"))), Seq(241)),
      242 -> NGrammar.NSequence(242, Symbols.Sequence(Seq(Symbols.ExactChar('\''), Symbols.Nonterminal("TerminalChoiceElem"), Symbols.Repeat(Symbols.Nonterminal("TerminalChoiceElem"), 1), Symbols.ExactChar('\''))), Seq(227, 243, 255, 227)),
      244 -> NGrammar.NSequence(244, Symbols.Sequence(Seq(Symbols.Nonterminal("TerminalChoiceChar"))), Seq(245)),
      246 -> NGrammar.NSequence(246, Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.Chars(Set('\'', '-', '\\'))))), Seq(247)),
      249 -> NGrammar.NSequence(249, Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.Chars(Set('\'', '-', '\\', 'b', 'n', 'r', 't')))), Seq(231, 250)),
      251 -> NGrammar.NSequence(251, Symbols.Sequence(Seq(Symbols.Nonterminal("TerminalChoiceRange"))), Seq(252)),
      253 -> NGrammar.NSequence(253, Symbols.Sequence(Seq(Symbols.Nonterminal("TerminalChoiceChar"), Symbols.ExactChar('-'), Symbols.Nonterminal("TerminalChoiceChar"))), Seq(245, 254, 245)),
      256 -> NGrammar.NSequence(256, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.Nonterminal("TerminalChoiceElem"), 1), Symbols.Nonterminal("TerminalChoiceElem"))), Seq(255, 243)),
      257 -> NGrammar.NSequence(257, Symbols.Sequence(Seq(Symbols.ExactChar('\''), Symbols.Nonterminal("TerminalChoiceRange"), Symbols.ExactChar('\''))), Seq(227, 252, 227)),
      258 -> NGrammar.NSequence(258, Symbols.Sequence(Seq(Symbols.Nonterminal("StringSymbol"))), Seq(259)),
      260 -> NGrammar.NSequence(260, Symbols.Sequence(Seq(Symbols.ExactChar('"'), Symbols.Repeat(Symbols.Nonterminal("StringChar"), 0), Symbols.ExactChar('"'))), Seq(261, 262, 261)),
      263 -> NGrammar.NSequence(263, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.Nonterminal("StringChar"), 0), Symbols.Nonterminal("StringChar"))), Seq(262, 264)),
      265 -> NGrammar.NSequence(265, Symbols.Sequence(Seq(Symbols.Except(Symbols.AnyChar, Symbols.Chars(Set('"', '\\'))))), Seq(266)),
      268 -> NGrammar.NSequence(268, Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.Chars(Set('"', '\\', 'b', 'n', 'r', 't')))), Seq(231, 269)),
      270 -> NGrammar.NSequence(270, Symbols.Sequence(Seq(Symbols.Nonterminal("Nonterminal"))), Seq(42)),
      271 -> NGrammar.NSequence(271, Symbols.Sequence(Seq(Symbols.ExactChar('('), Symbols.Nonterminal("WS"), Symbols.Nonterminal("InPlaceChoices"), Symbols.Nonterminal("WS"), Symbols.ExactChar(')'))), Seq(143, 4, 272, 4, 156)),
      273 -> NGrammar.NSequence(273, Symbols.Sequence(Seq(Symbols.Nonterminal("Sequence"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))))), 0))), Seq(201, 274)),
      275 -> NGrammar.NSequence(275, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence")))))))), Seq(274, 276)),
      278 -> NGrammar.NSequence(278, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("Sequence"))), Seq(4, 279, 4, 201)),
      280 -> NGrammar.NSequence(280, Symbols.Sequence(Seq(Symbols.Nonterminal("Longest"))), Seq(281)),
      282 -> NGrammar.NSequence(282, Symbols.Sequence(Seq(Symbols.ExactChar('<'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("InPlaceChoices"), Symbols.Nonterminal("WS"), Symbols.ExactChar('>'))), Seq(127, 4, 272, 4, 139)),
      283 -> NGrammar.NSequence(283, Symbols.Sequence(Seq(Symbols.Nonterminal("EmptySequence"))), Seq(284)),
      285 -> NGrammar.NSequence(285, Symbols.Sequence(Seq(Symbols.ExactChar('#'))), Seq(286)),
      287 -> NGrammar.NSequence(287, Symbols.Sequence(Seq(Symbols.Nonterminal("BinSymbol"), Symbols.Nonterminal("WS"), Symbols.ExactChar('-'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PreUnSymbol"))), Seq(207, 4, 254, 4, 210)),
      288 -> NGrammar.NSequence(288, Symbols.Sequence(Seq(Symbols.Nonterminal("PreUnSymbol"))), Seq(210)),
      289 -> NGrammar.NSequence(289, Symbols.Sequence(Seq(Symbols.Nonterminal("Processor"))), Seq(290)),
      291 -> NGrammar.NSequence(291, Symbols.Sequence(Seq(Symbols.Nonterminal("Ref"))), Seq(292)),
      293 -> NGrammar.NSequence(293, Symbols.Sequence(Seq(Symbols.Nonterminal("ValRef"))), Seq(294)),
      295 -> NGrammar.NSequence(295, Symbols.Sequence(Seq(Symbols.ExactChar('$'), Symbols.OneOf(ListSet(Symbols.Nonterminal("CondSymPath"), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("RefIdx"))), Seq(296, 297, 307)),
      299 -> NGrammar.NSequence(299, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))))), 1))), Seq(300)),
      303 -> NGrammar.NSequence(303, Symbols.Sequence(Seq(Symbols.ExactChar('<'))), Seq(127)),
      305 -> NGrammar.NSequence(305, Symbols.Sequence(Seq(Symbols.ExactChar('>'))), Seq(139)),
      306 -> NGrammar.NSequence(306, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))))), 1), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('<')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('>')))))))), Seq(300, 301)),
      308 -> NGrammar.NSequence(308, Symbols.Sequence(Seq(Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('0')))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(('1' to '9').toSet), Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0))))))))), Seq(309)),
      312 -> NGrammar.NSequence(312, Symbols.Sequence(Seq(Symbols.ExactChar('0'))), Seq(313)),
      315 -> NGrammar.NSequence(315, Symbols.Sequence(Seq(Symbols.Chars(('1' to '9').toSet), Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0))), Seq(316, 317)),
      318 -> NGrammar.NSequence(318, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.Chars(('0' to '9').toSet), 0), Symbols.Chars(('0' to '9').toSet))), Seq(317, 319)),
      320 -> NGrammar.NSequence(320, Symbols.Sequence(Seq(Symbols.Nonterminal("RawRef"))), Seq(321)),
      322 -> NGrammar.NSequence(322, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.ExactChar('$')))), Symbols.OneOf(ListSet(Symbols.Nonterminal("CondSymPath"), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("RefIdx"))), Seq(323, 297, 307)),
      324 -> NGrammar.NSequence(324, Symbols.Sequence(Seq(Symbols.ExactChar('\\'), Symbols.ExactChar('$'))), Seq(231, 296)),
      325 -> NGrammar.NSequence(325, Symbols.Sequence(Seq(Symbols.Nonterminal("PExprBlock"))), Seq(326)),
      327 -> NGrammar.NSequence(327, Symbols.Sequence(Seq(Symbols.ExactChar('{'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr"), Symbols.Nonterminal("WS"), Symbols.ExactChar('}'))), Seq(165, 4, 328, 4, 178)),
      329 -> NGrammar.NSequence(329, Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr"), Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc"))), Seq(330, 4, 99, 4, 100)),
      331 -> NGrammar.NSequence(331, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolOrExpr"), Symbols.Nonterminal("WS"), Symbols.ExactChar('?'), Symbols.Nonterminal("WS"), Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr"))))))), Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr"))))))))), Seq(332, 4, 194, 4, 436, 4, 99, 4, 436)),
      333 -> NGrammar.NSequence(333, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolAndExpr"), Symbols.Nonterminal("WS"), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('&'), Symbols.ExactChar('&')))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("BoolOrExpr"))), Seq(334, 4, 433, 4, 332)),
      335 -> NGrammar.NSequence(335, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolEqExpr"), Symbols.Nonterminal("WS"), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('|'), Symbols.ExactChar('|')))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("BoolAndExpr"))), Seq(336, 4, 430, 4, 334)),
      337 -> NGrammar.NSequence(337, Symbols.Sequence(Seq(Symbols.Nonterminal("ElvisExpr"), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('='))))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('='))))))))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("BoolEqExpr"))), Seq(338, 4, 420, 4, 336)),
      339 -> NGrammar.NSequence(339, Symbols.Sequence(Seq(Symbols.Nonterminal("AdditiveExpr"), Symbols.Nonterminal("WS"), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('?'), Symbols.ExactChar(':')))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("ElvisExpr"))), Seq(340, 4, 417, 4, 338)),
      341 -> NGrammar.NSequence(341, Symbols.Sequence(Seq(Symbols.Nonterminal("PrefixNotExpr"), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('+')))))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("AdditiveExpr"))), Seq(342, 4, 413, 4, 340)),
      343 -> NGrammar.NSequence(343, Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PrefixNotExpr"))), Seq(214, 4, 342)),
      344 -> NGrammar.NSequence(344, Symbols.Sequence(Seq(Symbols.Nonterminal("Atom"))), Seq(345)),
      346 -> NGrammar.NSequence(346, Symbols.Sequence(Seq(Symbols.Nonterminal("BindExpr"))), Seq(347)),
      348 -> NGrammar.NSequence(348, Symbols.Sequence(Seq(Symbols.Nonterminal("ValRef"), Symbols.Nonterminal("BinderExpr"))), Seq(294, 349)),
      350 -> NGrammar.NSequence(350, Symbols.Sequence(Seq(Symbols.Nonterminal("NamedConstructExpr"))), Seq(351)),
      352 -> NGrammar.NSequence(352, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeName"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("SuperTypes")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedConstructParams"))), Seq(104, 161, 4, 353)),
      354 -> NGrammar.NSequence(354, Symbols.Sequence(Seq(Symbols.ExactChar('('), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("NamedParam"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.ExactChar(')'))), Seq(143, 4, 355, 156)),
      357 -> NGrammar.NSequence(357, Symbols.Sequence(Seq(Symbols.Nonterminal("NamedParam"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), Symbols.Nonterminal("WS"))), Seq(358, 360, 4)),
      359 -> NGrammar.NSequence(359, Symbols.Sequence(Seq(Symbols.Nonterminal("ParamName"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(':'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("TypeDesc")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.Nonterminal("WS"), Symbols.ExactChar('='), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr"))), Seq(150, 95, 4, 195, 4, 328)),
      361 -> NGrammar.NSequence(361, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam")))))))), Seq(360, 362)),
      364 -> NGrammar.NSequence(364, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("NamedParam"))), Seq(4, 137, 4, 358)),
      365 -> NGrammar.NSequence(365, Symbols.Sequence(Seq(Symbols.Nonterminal("FuncCallOrConstructExpr"))), Seq(366)),
      367 -> NGrammar.NSequence(367, Symbols.Sequence(Seq(Symbols.Nonterminal("TypeOrFuncName"), Symbols.Nonterminal("WS"), Symbols.Nonterminal("CallParams"))), Seq(368, 4, 369)),
      370 -> NGrammar.NSequence(370, Symbols.Sequence(Seq(Symbols.ExactChar('('), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.ExactChar(')'))), Seq(143, 4, 371, 156)),
      374 -> NGrammar.NSequence(374, Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS"))), Seq(328, 375, 4)),
      376 -> NGrammar.NSequence(376, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))))), Seq(375, 377)),
      379 -> NGrammar.NSequence(379, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr"))), Seq(4, 137, 4, 328)),
      380 -> NGrammar.NSequence(380, Symbols.Sequence(Seq(Symbols.Nonterminal("ArrayExpr"))), Seq(381)),
      382 -> NGrammar.NSequence(382, Symbols.Sequence(Seq(Symbols.ExactChar('['), Symbols.Nonterminal("WS"), Symbols.OneOf(ListSet(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("PExpr"), Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar(','), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr")))))), 0), Symbols.Nonterminal("WS")))))), Symbols.Proxy(Symbols.Sequence(Seq())))), Symbols.ExactChar(']'))), Seq(106, 4, 371, 107)),
      383 -> NGrammar.NSequence(383, Symbols.Sequence(Seq(Symbols.Nonterminal("Literal"))), Seq(384)),
      385 -> NGrammar.NSequence(385, Symbols.Sequence(Seq(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('t'), Symbols.ExactChar('r'), Symbols.ExactChar('u'), Symbols.ExactChar('e'))))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('f'), Symbols.ExactChar('a'), Symbols.ExactChar('l'), Symbols.ExactChar('s'), Symbols.ExactChar('e'))))))))))), Seq(386)),
      389 -> NGrammar.NSequence(389, Symbols.Sequence(Seq(Symbols.ExactChar('\''), Symbols.Nonterminal("CharChar"), Symbols.ExactChar('\''))), Seq(227, 390, 227)),
      391 -> NGrammar.NSequence(391, Symbols.Sequence(Seq(Symbols.Nonterminal("TerminalChar"))), Seq(228)),
      392 -> NGrammar.NSequence(392, Symbols.Sequence(Seq(Symbols.ExactChar('"'), Symbols.Repeat(Symbols.Nonterminal("StrChar"), 0), Symbols.ExactChar('"'))), Seq(261, 393, 261)),
      394 -> NGrammar.NSequence(394, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.Nonterminal("StrChar"), 0), Symbols.Nonterminal("StrChar"))), Seq(393, 395)),
      396 -> NGrammar.NSequence(396, Symbols.Sequence(Seq(Symbols.Nonterminal("StringChar"))), Seq(264)),
      397 -> NGrammar.NSequence(397, Symbols.Sequence(Seq(Symbols.Nonterminal("EnumValue"))), Seq(398)),
      399 -> NGrammar.NSequence(399, Symbols.Sequence(Seq(Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("CanonicalEnumValue")))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("ShortenedEnumValue"))))))))), Seq(400)),
      403 -> NGrammar.NSequence(403, Symbols.Sequence(Seq(Symbols.Nonterminal("CanonicalEnumValue"))), Seq(404)),
      405 -> NGrammar.NSequence(405, Symbols.Sequence(Seq(Symbols.Nonterminal("EnumTypeName"), Symbols.ExactChar('.'), Symbols.Nonterminal("EnumValueName"))), Seq(117, 239, 406)),
      407 -> NGrammar.NSequence(407, Symbols.Sequence(Seq(Symbols.Nonterminal("Id"))), Seq(49)),
      409 -> NGrammar.NSequence(409, Symbols.Sequence(Seq(Symbols.Nonterminal("ShortenedEnumValue"))), Seq(410)),
      411 -> NGrammar.NSequence(411, Symbols.Sequence(Seq(Symbols.ExactChar('%'), Symbols.Nonterminal("EnumValueName"))), Seq(119, 406)),
      412 -> NGrammar.NSequence(412, Symbols.Sequence(Seq(Symbols.ExactChar('('), Symbols.Nonterminal("WS"), Symbols.Nonterminal("PExpr"), Symbols.Nonterminal("WS"), Symbols.ExactChar(')'))), Seq(143, 4, 328, 4, 156)),
      415 -> NGrammar.NSequence(415, Symbols.Sequence(Seq(Symbols.ExactChar('+'))), Seq(221)),
      416 -> NGrammar.NSequence(416, Symbols.Sequence(Seq(Symbols.Nonterminal("PrefixNotExpr"))), Seq(342)),
      418 -> NGrammar.NSequence(418, Symbols.Sequence(Seq(Symbols.ExactChar('?'), Symbols.ExactChar(':'))), Seq(194, 99)),
      419 -> NGrammar.NSequence(419, Symbols.Sequence(Seq(Symbols.Nonterminal("AdditiveExpr"))), Seq(340)),
      422 -> NGrammar.NSequence(422, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('=')))))), Seq(423)),
      424 -> NGrammar.NSequence(424, Symbols.Sequence(Seq(Symbols.ExactChar('='), Symbols.ExactChar('='))), Seq(195, 195)),
      426 -> NGrammar.NSequence(426, Symbols.Sequence(Seq(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('=')))))), Seq(427)),
      428 -> NGrammar.NSequence(428, Symbols.Sequence(Seq(Symbols.ExactChar('!'), Symbols.ExactChar('='))), Seq(214, 195)),
      429 -> NGrammar.NSequence(429, Symbols.Sequence(Seq(Symbols.Nonterminal("ElvisExpr"))), Seq(338)),
      431 -> NGrammar.NSequence(431, Symbols.Sequence(Seq(Symbols.ExactChar('|'), Symbols.ExactChar('|'))), Seq(279, 279)),
      432 -> NGrammar.NSequence(432, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolEqExpr"))), Seq(336)),
      434 -> NGrammar.NSequence(434, Symbols.Sequence(Seq(Symbols.ExactChar('&'), Symbols.ExactChar('&'))), Seq(209, 209)),
      435 -> NGrammar.NSequence(435, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolAndExpr"))), Seq(334)),
      439 -> NGrammar.NSequence(439, Symbols.Sequence(Seq(Symbols.Nonterminal("TernaryExpr"))), Seq(330)),
      440 -> NGrammar.NSequence(440, Symbols.Sequence(Seq(Symbols.Nonterminal("BoolOrExpr"))), Seq(332)),
      442 -> NGrammar.NSequence(442, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem")))))))), Seq(441, 443)),
      445 -> NGrammar.NSequence(445, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.Nonterminal("Elem"))), Seq(4, 203)),
      447 -> NGrammar.NSequence(447, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS")))))))), Seq(446, 448)),
      450 -> NGrammar.NSequence(450, Symbols.Sequence(Seq(Symbols.Nonterminal("WS"), Symbols.ExactChar('|'), Symbols.Nonterminal("WS"), Symbols.Nonterminal("RHS"))), Seq(4, 279, 4, 199)),
      452 -> NGrammar.NSequence(452, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def")))))))), Seq(451, 453)),
      455 -> NGrammar.NSequence(455, Symbols.Sequence(Seq(Symbols.Nonterminal("WSNL"), Symbols.Nonterminal("Def"))), Seq(456, 36)),
      457 -> NGrammar.NSequence(457, Symbols.Sequence(Seq(Symbols.Longest(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.ExactChar('\n'), Symbols.Nonterminal("WS"))))))))), Seq(458)),
      461 -> NGrammar.NSequence(461, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.ExactChar('\n'), Symbols.Nonterminal("WS"))), Seq(462, 27, 4)),
      463 -> NGrammar.NSequence(463, Symbols.Sequence(Seq(Symbols.Repeat(Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))), 0), Symbols.OneOf(ListSet(Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' '))))), Symbols.Proxy(Symbols.Sequence(Seq(Symbols.Nonterminal("LineComment")))))))), Seq(462, 464)),
      466 -> NGrammar.NSequence(466, Symbols.Sequence(Seq(Symbols.Chars(Set('\t', '\r', ' ')))), Seq(467))),
    1)

  sealed trait WithParseNode {
    val parseNode: Node
  }

  case class AbstractClassDef(name: TypeName, supers: List[TypeName])(override val parseNode: Node) extends ClassDef with WithParseNode

  sealed trait AbstractEnumValue extends Atom with WithParseNode

  sealed trait AdditiveExpr extends ElvisExpr with WithParseNode

  case class AnyTerminal()(override val parseNode: Node) extends Terminal with WithParseNode

  case class AnyType()(override val parseNode: Node) extends NonNullTypeDesc with WithParseNode

  case class ArrayExpr(elems: List[PExpr])(override val parseNode: Node) extends Atom with WithParseNode

  case class ArrayTypeDesc(elemType: TypeDesc)(override val parseNode: Node) extends NonNullTypeDesc with WithParseNode

  sealed trait Atom extends PrefixNotExpr with WithParseNode

  sealed trait AtomSymbol extends PostUnSymbol with WithParseNode

  case class BinOp(op: Op.Value, lhs: BoolAndExpr, rhs: BoolOrExpr)(override val parseNode: Node) extends AdditiveExpr with WithParseNode

  sealed trait BinSymbol extends Symbol with WithParseNode

  case class BindExpr(ctx: ValRef, binder: BinderExpr)(override val parseNode: Node) extends Atom with BinderExpr with WithParseNode

  sealed trait BinderExpr extends WithParseNode

  sealed trait BoolAndExpr extends BoolOrExpr with WithParseNode

  sealed trait BoolEqExpr extends BoolAndExpr with WithParseNode

  case class BoolLiteral(value: Boolean)(override val parseNode: Node) extends Literal with WithParseNode

  sealed trait BoolOrExpr extends TernaryExpr with WithParseNode

  case class BooleanType()(override val parseNode: Node) extends ValueType with WithParseNode

  case class CanonicalEnumValue(enumName: EnumTypeName, valueName: EnumValueName)(override val parseNode: Node) extends AbstractEnumValue with WithParseNode

  case class CharAsIs(value: Char)(override val parseNode: Node) extends StringChar with TerminalChar with TerminalChoiceChar with WithParseNode

  case class CharEscaped(escapeCode: Char)(override val parseNode: Node) extends StringChar with TerminalChar with TerminalChoiceChar with WithParseNode

  case class CharLiteral(value: TerminalChar)(override val parseNode: Node) extends Literal with WithParseNode

  case class CharType()(override val parseNode: Node) extends ValueType with WithParseNode

  case class CharUnicode(code: List[Char])(override val parseNode: Node) extends StringChar with TerminalChar with TerminalChoiceChar with WithParseNode

  sealed trait ClassDef extends SubType with TypeDef with WithParseNode

  case class ClassParamDef(name: ParamName, typeDesc: Option[TypeDesc])(override val parseNode: Node) extends WithParseNode

  case class ConcreteClassDef(name: TypeName, supers: Option[List[TypeName]], params: List[ClassParamDef])(override val parseNode: Node) extends ClassDef with WithParseNode

  sealed trait Def extends WithParseNode

  sealed trait Elem extends WithParseNode

  sealed trait ElvisExpr extends BoolEqExpr with WithParseNode

  case class ElvisOp(value: AdditiveExpr, ifNull: ElvisExpr)(override val parseNode: Node) extends ElvisExpr with WithParseNode

  case class EmptySeq()(override val parseNode: Node) extends AtomSymbol with WithParseNode

  case class EnumTypeDef(name: EnumTypeName, values: List[String])(override val parseNode: Node) extends TypeDef with WithParseNode

  case class EnumTypeName(name: String)(override val parseNode: Node) extends NonNullTypeDesc with WithParseNode

  case class EnumValueName(name: String)(override val parseNode: Node) extends WithParseNode

  case class ExceptSymbol(body: BinSymbol, except: PreUnSymbol)(override val parseNode: Node) extends BinSymbol with WithParseNode

  case class ExprParen(body: PExpr)(override val parseNode: Node) extends Atom with WithParseNode

  case class FollowedBy(followedBy: PreUnSymbol)(override val parseNode: Node) extends PreUnSymbol with WithParseNode

  case class FuncCallOrConstructExpr(funcName: TypeOrFuncName, params: List[PExpr])(override val parseNode: Node) extends Atom with WithParseNode

  case class Grammar(defs: List[Def])(override val parseNode: Node) extends WithParseNode

  case class InPlaceChoices(choices: List[Sequence])(override val parseNode: Node) extends AtomSymbol with WithParseNode

  case class JoinSymbol(body: BinSymbol, join: PreUnSymbol)(override val parseNode: Node) extends BinSymbol with WithParseNode

  case class LHS(name: Nonterminal, typeDesc: Option[TypeDesc])(override val parseNode: Node) extends WithParseNode

  sealed trait Literal extends Atom with WithParseNode

  case class Longest(choices: InPlaceChoices)(override val parseNode: Node) extends AtomSymbol with WithParseNode

  case class NamedConstructExpr(typeName: TypeName, params: List[NamedParam], supers: Option[List[TypeName]])(override val parseNode: Node) extends Atom with WithParseNode

  case class NamedParam(name: ParamName, typeDesc: Option[TypeDesc], expr: PExpr)(override val parseNode: Node) extends WithParseNode

  sealed trait NonNullTypeDesc extends WithParseNode

  case class Nonterminal(name: NonterminalName)(override val parseNode: Node) extends AtomSymbol with WithParseNode

  case class NonterminalName(name: String)(override val parseNode: Node) extends WithParseNode

  case class NotFollowedBy(notFollowedBy: PreUnSymbol)(override val parseNode: Node) extends PreUnSymbol with WithParseNode

  case class NullLiteral()(override val parseNode: Node) extends Literal with WithParseNode

  case class Optional(body: PostUnSymbol)(override val parseNode: Node) extends PostUnSymbol with WithParseNode

  sealed trait PExpr extends WithParseNode

  case class ParamName(name: String)(override val parseNode: Node) extends WithParseNode

  sealed trait PostUnSymbol extends PreUnSymbol with WithParseNode

  sealed trait PreUnSymbol extends BinSymbol with WithParseNode

  sealed trait PrefixNotExpr extends AdditiveExpr with WithParseNode

  case class PrefixOp(op: PreOp.Value, expr: PrefixNotExpr)(override val parseNode: Node) extends PrefixNotExpr with WithParseNode

  sealed trait Processor extends Elem with WithParseNode

  case class ProcessorBlock(body: PExpr)(override val parseNode: Node) extends BinderExpr with Processor with WithParseNode

  case class RawRef(idx: String, condSymPath: Option[List[CondSymDir.Value]])(override val parseNode: Node) extends Ref with WithParseNode

  sealed trait Ref extends Atom with BinderExpr with Processor with WithParseNode

  case class RepeatFromOne(body: PostUnSymbol)(override val parseNode: Node) extends PostUnSymbol with WithParseNode

  case class RepeatFromZero(body: PostUnSymbol)(override val parseNode: Node) extends PostUnSymbol with WithParseNode

  case class Rule(lhs: LHS, rhs: List[Sequence])(override val parseNode: Node) extends Def with WithParseNode

  case class Sequence(seq: List[Elem])(override val parseNode: Node) extends Symbol with WithParseNode

  case class ShortenedEnumValue(valueName: EnumValueName)(override val parseNode: Node) extends AbstractEnumValue with WithParseNode

  case class StrLiteral(value: List[StringChar])(override val parseNode: Node) extends Literal with WithParseNode

  sealed trait StringChar extends WithParseNode

  case class StringSymbol(value: List[StringChar])(override val parseNode: Node) extends AtomSymbol with WithParseNode

  case class StringType()(override val parseNode: Node) extends ValueType with WithParseNode

  sealed trait SubType extends WithParseNode

  case class SuperDef(typeName: TypeName, subs: Option[List[SubType]], supers: Option[List[TypeName]])(override val parseNode: Node) extends SubType with TypeDef with WithParseNode

  sealed trait Symbol extends Elem with WithParseNode

  sealed trait Terminal extends AtomSymbol with WithParseNode

  sealed trait TerminalChar extends Terminal with WithParseNode

  case class TerminalChoice(choices: List[TerminalChoiceElem])(override val parseNode: Node) extends AtomSymbol with WithParseNode

  sealed trait TerminalChoiceChar extends TerminalChoiceElem with WithParseNode

  sealed trait TerminalChoiceElem extends WithParseNode

  case class TerminalChoiceRange(start: TerminalChoiceChar, end: TerminalChoiceChar)(override val parseNode: Node) extends TerminalChoiceElem with WithParseNode

  sealed trait TernaryExpr extends PExpr with WithParseNode

  case class TernaryOp(cond: BoolOrExpr, ifTrue: TernaryExpr, ifFalse: TernaryExpr)(override val parseNode: Node) extends TernaryExpr with WithParseNode

  sealed trait TypeDef extends Def with NonNullTypeDesc with WithParseNode

  case class TypeDesc(typ: NonNullTypeDesc, optional: Boolean)(override val parseNode: Node) extends WithParseNode

  case class TypeName(name: String)(override val parseNode: Node) extends NonNullTypeDesc with SubType with WithParseNode

  case class TypeOrFuncName(name: String)(override val parseNode: Node) extends WithParseNode

  case class TypedPExpr(body: TernaryExpr, typ: TypeDesc)(override val parseNode: Node) extends PExpr with WithParseNode

  case class ValRef(idx: String, condSymPath: Option[List[CondSymDir.Value]])(override val parseNode: Node) extends Ref with WithParseNode

  sealed trait ValueType extends NonNullTypeDesc with WithParseNode

  object CondSymDir extends Enumeration {
    val BODY, COND = Value
  }

  object KeyWord extends Enumeration {
    val BOOLEAN, CHAR, FALSE, NULL, STRING, TRUE = Value
  }

  object Op extends Enumeration {
    val ADD, AND, EQ, NE, OR = Value
  }

  object PreOp extends Enumeration {
    val NOT = Value
  }

  def matchAdditiveExpr(node: Node): AdditiveExpr = {
    val BindNode(v1, v2) = node
    val v20 = v1.id match {
      case 341 =>
        val v3 = v2.asInstanceOf[SequenceNode].children(2)
        val BindNode(v4, v5) = v3
        assert(v4.id == 413)
        val BindNode(v6, v7) = v5
        val v10 = v6.id match {
          case 414 =>
            val BindNode(v8, v9) = v7
            assert(v8.id == 415)
            Op.ADD
        }
        val v11 = v2.asInstanceOf[SequenceNode].children.head
        val BindNode(v12, v13) = v11
        assert(v12.id == 342)
        val v14 = v2.asInstanceOf[SequenceNode].children(4)
        val BindNode(v15, v16) = v14
        assert(v15.id == 340)
        BinOp(v10, matchPrefixNotExpr(v13), matchAdditiveExpr(v16))(v2)
      case 416 =>
        val v17 = v2.asInstanceOf[SequenceNode].children.head
        val BindNode(v18, v19) = v17
        assert(v18.id == 342)
        matchPrefixNotExpr(v19)
    }
    v20
  }

  def matchAnyType(node: Node): AnyType = {
    val BindNode(v21, v22) = node
    val v23 = v21.id match {
      case 112 =>
        AnyType()(v22)
    }
    v23
  }

  def matchArrayExpr(node: Node): ArrayExpr = {
    val BindNode(v24, v25) = node
    val v52 = v24.id match {
      case 382 =>
        val v27 = v25.asInstanceOf[SequenceNode].children(2)
        val BindNode(v28, v29) = v27
        assert(v28.id == 371)
        val BindNode(v30, v31) = v29
        val v51 = v30.id match {
          case 138 =>
            None
          case 372 =>
            val BindNode(v32, v33) = v31
            assert(v32.id == 373)
            val BindNode(v34, v35) = v33
            assert(v34.id == 374)
            val v36 = v35.asInstanceOf[SequenceNode].children.head
            val BindNode(v37, v38) = v36
            assert(v37.id == 328)
            val v39 = v35.asInstanceOf[SequenceNode].children(1)
            val v40 = unrollRepeat0(v39).map { elem =>
              val BindNode(v41, v42) = elem
              assert(v41.id == 377)
              val BindNode(v43, v44) = v42
              val v50 = v43.id match {
                case 378 =>
                  val BindNode(v45, v46) = v44
                  assert(v45.id == 379)
                  val v47 = v46.asInstanceOf[SequenceNode].children(3)
                  val BindNode(v48, v49) = v47
                  assert(v48.id == 328)
                  matchPExpr(v49)
              }
              v50
            }
            Some(List(matchPExpr(v38)) ++ v40)
        }
        val v26 = v51
        ArrayExpr(if (v26.isDefined) v26.get else List())(v25)
    }
    v52
  }

  def matchAtom(node: Node): Atom = {
    val BindNode(v53, v54) = node
    val v79 = v53.id match {
      case 291 =>
        val v55 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v56, v57) = v55
        assert(v56.id == 292)
        matchRef(v57)
      case 350 =>
        val v58 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v59, v60) = v58
        assert(v59.id == 351)
        matchNamedConstructExpr(v60)
      case 383 =>
        val v61 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v62, v63) = v61
        assert(v62.id == 384)
        matchLiteral(v63)
      case 380 =>
        val v64 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v65, v66) = v64
        assert(v65.id == 381)
        matchArrayExpr(v66)
      case 412 =>
        val v67 = v54.asInstanceOf[SequenceNode].children(2)
        val BindNode(v68, v69) = v67
        assert(v68.id == 328)
        ExprParen(matchPExpr(v69))(v54)
      case 346 =>
        val v70 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v71, v72) = v70
        assert(v71.id == 347)
        matchBindExpr(v72)
      case 397 =>
        val v73 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v74, v75) = v73
        assert(v74.id == 398)
        matchEnumValue(v75)
      case 365 =>
        val v76 = v54.asInstanceOf[SequenceNode].children.head
        val BindNode(v77, v78) = v76
        assert(v77.id == 366)
        matchFuncCallOrConstructExpr(v78)
    }
    v79
  }

  def matchAtomSymbol(node: Node): AtomSymbol = {
    val BindNode(v80, v81) = node
    val v103 = v80.id match {
      case 224 =>
        val v82 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v83, v84) = v82
        assert(v83.id == 225)
        matchTerminal(v84)
      case 240 =>
        val v85 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v86, v87) = v85
        assert(v86.id == 241)
        matchTerminalChoice(v87)
      case 283 =>
        val v88 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v89, v90) = v88
        assert(v89.id == 284)
        matchEmptySequence(v90)
      case 270 =>
        val v91 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v92, v93) = v91
        assert(v92.id == 42)
        matchNonterminal(v93)
      case 271 =>
        val v94 = v81.asInstanceOf[SequenceNode].children(2)
        val BindNode(v95, v96) = v94
        assert(v95.id == 272)
        matchInPlaceChoices(v96)
      case 280 =>
        val v97 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v98, v99) = v97
        assert(v98.id == 281)
        matchLongest(v99)
      case 258 =>
        val v100 = v81.asInstanceOf[SequenceNode].children.head
        val BindNode(v101, v102) = v100
        assert(v101.id == 259)
        matchStringSymbol(v102)
    }
    v103
  }

  def matchBinSymbol(node: Node): BinSymbol = {
    val BindNode(v104, v105) = node
    val v121 = v104.id match {
      case 208 =>
        val v106 = v105.asInstanceOf[SequenceNode].children.head
        val BindNode(v107, v108) = v106
        assert(v107.id == 207)
        val v109 = v105.asInstanceOf[SequenceNode].children(4)
        val BindNode(v110, v111) = v109
        assert(v110.id == 210)
        JoinSymbol(matchBinSymbol(v108), matchPreUnSymbol(v111))(v105)
      case 287 =>
        val v112 = v105.asInstanceOf[SequenceNode].children.head
        val BindNode(v113, v114) = v112
        assert(v113.id == 207)
        val v115 = v105.asInstanceOf[SequenceNode].children(4)
        val BindNode(v116, v117) = v115
        assert(v116.id == 210)
        ExceptSymbol(matchBinSymbol(v114), matchPreUnSymbol(v117))(v105)
      case 288 =>
        val v118 = v105.asInstanceOf[SequenceNode].children.head
        val BindNode(v119, v120) = v118
        assert(v119.id == 210)
        matchPreUnSymbol(v120)
    }
    v121
  }

  def matchBindExpr(node: Node): BindExpr = {
    val BindNode(v122, v123) = node
    val v130 = v122.id match {
      case 348 =>
        val v124 = v123.asInstanceOf[SequenceNode].children.head
        val BindNode(v125, v126) = v124
        assert(v125.id == 294)
        val v127 = v123.asInstanceOf[SequenceNode].children(1)
        val BindNode(v128, v129) = v127
        assert(v128.id == 349)
        BindExpr(matchValRef(v126), matchBinderExpr(v129))(v123)
    }
    v130
  }

  def matchBinderExpr(node: Node): BinderExpr = {
    val BindNode(v131, v132) = node
    val v142 = v131.id match {
      case 291 =>
        val v133 = v132.asInstanceOf[SequenceNode].children.head
        val BindNode(v134, v135) = v133
        assert(v134.id == 292)
        matchRef(v135)
      case 346 =>
        val v136 = v132.asInstanceOf[SequenceNode].children.head
        val BindNode(v137, v138) = v136
        assert(v137.id == 347)
        matchBindExpr(v138)
      case 325 =>
        val v139 = v132.asInstanceOf[SequenceNode].children.head
        val BindNode(v140, v141) = v139
        assert(v140.id == 326)
        matchPExprBlock(v141)
    }
    v142
  }

  def matchBoolAndExpr(node: Node): BoolAndExpr = {
    val BindNode(v143, v144) = node
    val v154 = v143.id match {
      case 335 =>
        val v145 = v144.asInstanceOf[SequenceNode].children.head
        val BindNode(v146, v147) = v145
        assert(v146.id == 336)
        val v148 = v144.asInstanceOf[SequenceNode].children(4)
        val BindNode(v149, v150) = v148
        assert(v149.id == 334)
        BinOp(Op.OR, matchBoolEqExpr(v147), matchBoolAndExpr(v150))(v144)
      case 432 =>
        val v151 = v144.asInstanceOf[SequenceNode].children.head
        val BindNode(v152, v153) = v151
        assert(v152.id == 336)
        matchBoolEqExpr(v153)
    }
    v154
  }

  def matchBoolEqExpr(node: Node): BoolEqExpr = {
    val BindNode(v155, v156) = node
    val v176 = v155.id match {
      case 337 =>
        val v157 = v156.asInstanceOf[SequenceNode].children(2)
        val BindNode(v158, v159) = v157
        assert(v158.id == 420)
        val BindNode(v160, v161) = v159
        val v166 = v160.id match {
          case 421 =>
            val BindNode(v162, v163) = v161
            assert(v162.id == 422)
            Op.EQ
          case 425 =>
            val BindNode(v164, v165) = v161
            assert(v164.id == 426)
            Op.NE
        }
        val v167 = v156.asInstanceOf[SequenceNode].children.head
        val BindNode(v168, v169) = v167
        assert(v168.id == 338)
        val v170 = v156.asInstanceOf[SequenceNode].children(4)
        val BindNode(v171, v172) = v170
        assert(v171.id == 336)
        BinOp(v166, matchElvisExpr(v169), matchBoolEqExpr(v172))(v156)
      case 429 =>
        val v173 = v156.asInstanceOf[SequenceNode].children.head
        val BindNode(v174, v175) = v173
        assert(v174.id == 338)
        matchElvisExpr(v175)
    }
    v176
  }

  def matchBoolOrExpr(node: Node): BoolOrExpr = {
    val BindNode(v177, v178) = node
    val v188 = v177.id match {
      case 333 =>
        val v179 = v178.asInstanceOf[SequenceNode].children.head
        val BindNode(v180, v181) = v179
        assert(v180.id == 334)
        val v182 = v178.asInstanceOf[SequenceNode].children(4)
        val BindNode(v183, v184) = v182
        assert(v183.id == 332)
        BinOp(Op.AND, matchBoolAndExpr(v181), matchBoolOrExpr(v184))(v178)
      case 435 =>
        val v185 = v178.asInstanceOf[SequenceNode].children.head
        val BindNode(v186, v187) = v185
        assert(v186.id == 334)
        matchBoolAndExpr(v187)
    }
    v188
  }

  def matchCallParams(node: Node): List[PExpr] = {
    val BindNode(v189, v190) = node
    val v218 = v189.id match {
      case 370 =>
        val v192 = v190.asInstanceOf[SequenceNode].children(2)
        val BindNode(v193, v194) = v192
        assert(v193.id == 371)
        val BindNode(v195, v196) = v194
        val v217 = v195.id match {
          case 138 =>
            None
          case 372 =>
            val BindNode(v197, v198) = v196
            val v216 = v197.id match {
              case 373 =>
                val BindNode(v199, v200) = v198
                assert(v199.id == 374)
                val v201 = v200.asInstanceOf[SequenceNode].children.head
                val BindNode(v202, v203) = v201
                assert(v202.id == 328)
                val v204 = v200.asInstanceOf[SequenceNode].children(1)
                val v205 = unrollRepeat0(v204).map { elem =>
                  val BindNode(v206, v207) = elem
                  assert(v206.id == 377)
                  val BindNode(v208, v209) = v207
                  val v215 = v208.id match {
                    case 378 =>
                      val BindNode(v210, v211) = v209
                      assert(v210.id == 379)
                      val v212 = v211.asInstanceOf[SequenceNode].children(3)
                      val BindNode(v213, v214) = v212
                      assert(v213.id == 328)
                      matchPExpr(v214)
                  }
                  v215
                }
                List(matchPExpr(v203)) ++ v205
            }
            Some(v216)
        }
        val v191 = v217
        if (v191.isDefined) v191.get else List()
    }
    v218
  }

  def matchCanonicalEnumValue(node: Node): CanonicalEnumValue = {
    val BindNode(v219, v220) = node
    val v227 = v219.id match {
      case 405 =>
        val v221 = v220.asInstanceOf[SequenceNode].children.head
        val BindNode(v222, v223) = v221
        assert(v222.id == 117)
        val v224 = v220.asInstanceOf[SequenceNode].children(2)
        val BindNode(v225, v226) = v224
        assert(v225.id == 406)
        CanonicalEnumValue(matchEnumTypeName(v223), matchEnumValueName(v226))(v220)
    }
    v227
  }

  def matchCharChar(node: Node): TerminalChar = {
    val BindNode(v228, v229) = node
    val v233 = v228.id match {
      case 391 =>
        val v230 = v229.asInstanceOf[SequenceNode].children.head
        val BindNode(v231, v232) = v230
        assert(v231.id == 228)
        matchTerminalChar(v232)
    }
    v233
  }

  def matchClassDef(node: Node): ClassDef = {
    val BindNode(v234, v235) = node
    val v257 = v234.id match {
      case 124 =>
        val v236 = v235.asInstanceOf[SequenceNode].children.head
        val BindNode(v237, v238) = v236
        assert(v237.id == 104)
        val v239 = v235.asInstanceOf[SequenceNode].children(2)
        val BindNode(v240, v241) = v239
        assert(v240.id == 125)
        AbstractClassDef(matchTypeName(v238), matchSuperTypes(v241))(v235)
      case 140 =>
        val v242 = v235.asInstanceOf[SequenceNode].children.head
        val BindNode(v243, v244) = v242
        assert(v243.id == 104)
        val v245 = v235.asInstanceOf[SequenceNode].children(2)
        val BindNode(v246, v247) = v245
        assert(v246.id == 141)
        ConcreteClassDef(matchTypeName(v244), None, matchClassParamsDef(v247))(v235)
      case 157 =>
        val v248 = v235.asInstanceOf[SequenceNode].children.head
        val BindNode(v249, v250) = v248
        assert(v249.id == 104)
        val v251 = v235.asInstanceOf[SequenceNode].children(2)
        val BindNode(v252, v253) = v251
        assert(v252.id == 125)
        val v254 = v235.asInstanceOf[SequenceNode].children(4)
        val BindNode(v255, v256) = v254
        assert(v255.id == 141)
        ConcreteClassDef(matchTypeName(v250), Some(matchSuperTypes(v253)), matchClassParamsDef(v256))(v235)
    }
    v257
  }

  def matchClassParamDef(node: Node): ClassParamDef = {
    val BindNode(v258, v259) = node
    val v277 = v258.id match {
      case 149 =>
        val v260 = v259.asInstanceOf[SequenceNode].children.head
        val BindNode(v261, v262) = v260
        assert(v261.id == 150)
        val v263 = v259.asInstanceOf[SequenceNode].children(1)
        val BindNode(v264, v265) = v263
        assert(v264.id == 95)
        val BindNode(v266, v267) = v265
        val v276 = v266.id match {
          case 138 =>
            None
          case 96 =>
            val BindNode(v268, v269) = v267
            val v275 = v268.id match {
              case 97 =>
                val BindNode(v270, v271) = v269
                assert(v270.id == 98)
                val v272 = v271.asInstanceOf[SequenceNode].children(3)
                val BindNode(v273, v274) = v272
                assert(v273.id == 100)
                matchTypeDesc(v274)
            }
            Some(v275)
        }
        ClassParamDef(matchParamName(v262), v276)(v259)
    }
    v277
  }

  def matchClassParamsDef(node: Node): List[ClassParamDef] = {
    val BindNode(v278, v279) = node
    val v307 = v278.id match {
      case 142 =>
        val v281 = v279.asInstanceOf[SequenceNode].children(2)
        val BindNode(v282, v283) = v281
        assert(v282.id == 144)
        val BindNode(v284, v285) = v283
        val v306 = v284.id match {
          case 138 =>
            None
          case 145 =>
            val BindNode(v286, v287) = v285
            val v305 = v286.id match {
              case 146 =>
                val BindNode(v288, v289) = v287
                assert(v288.id == 147)
                val v290 = v289.asInstanceOf[SequenceNode].children.head
                val BindNode(v291, v292) = v290
                assert(v291.id == 148)
                val v293 = v289.asInstanceOf[SequenceNode].children(1)
                val v294 = unrollRepeat0(v293).map { elem =>
                  val BindNode(v295, v296) = elem
                  assert(v295.id == 153)
                  val BindNode(v297, v298) = v296
                  val v304 = v297.id match {
                    case 154 =>
                      val BindNode(v299, v300) = v298
                      assert(v299.id == 155)
                      val v301 = v300.asInstanceOf[SequenceNode].children(3)
                      val BindNode(v302, v303) = v301
                      assert(v302.id == 148)
                      matchClassParamDef(v303)
                  }
                  v304
                }
                List(matchClassParamDef(v292)) ++ v294
            }
            Some(v305)
        }
        val v280 = v306
        if (v280.isDefined) v280.get else List()
    }
    v307
  }

  def matchCondSymPath(node: Node): List[CondSymDir.Value] = {
    val BindNode(v308, v309) = node
    val v321 = v308.id match {
      case 299 =>
        val v310 = v309.asInstanceOf[SequenceNode].children.head
        val v311 = unrollRepeat1(v310).map { elem =>
          val BindNode(v312, v313) = elem
          assert(v312.id == 301)
          val BindNode(v314, v315) = v313
          val v320 = v314.id match {
            case 302 =>
              val BindNode(v316, v317) = v315
              assert(v316.id == 303)
              CondSymDir.BODY
            case 304 =>
              val BindNode(v318, v319) = v315
              assert(v318.id == 305)
              CondSymDir.COND
          }
          v320
        }
        v311
    }
    v321
  }

  def matchDef(node: Node): Def = {
    val BindNode(v322, v323) = node
    val v330 = v322.id match {
      case 37 =>
        val v324 = v323.asInstanceOf[SequenceNode].children.head
        val BindNode(v325, v326) = v324
        assert(v325.id == 38)
        matchRule(v326)
      case 120 =>
        val v327 = v323.asInstanceOf[SequenceNode].children.head
        val BindNode(v328, v329) = v327
        assert(v328.id == 121)
        matchTypeDef(v329)
    }
    v330
  }

  def matchElem(node: Node): Elem = {
    val BindNode(v331, v332) = node
    val v339 = v331.id match {
      case 204 =>
        val v333 = v332.asInstanceOf[SequenceNode].children.head
        val BindNode(v334, v335) = v333
        assert(v334.id == 205)
        matchSymbol(v335)
      case 289 =>
        val v336 = v332.asInstanceOf[SequenceNode].children.head
        val BindNode(v337, v338) = v336
        assert(v337.id == 290)
        matchProcessor(v338)
    }
    v339
  }

  def matchElvisExpr(node: Node): ElvisExpr = {
    val BindNode(v340, v341) = node
    val v351 = v340.id match {
      case 339 =>
        val v342 = v341.asInstanceOf[SequenceNode].children.head
        val BindNode(v343, v344) = v342
        assert(v343.id == 340)
        val v345 = v341.asInstanceOf[SequenceNode].children(4)
        val BindNode(v346, v347) = v345
        assert(v346.id == 338)
        ElvisOp(matchAdditiveExpr(v344), matchElvisExpr(v347))(v341)
      case 419 =>
        val v348 = v341.asInstanceOf[SequenceNode].children.head
        val BindNode(v349, v350) = v348
        assert(v349.id == 340)
        matchAdditiveExpr(v350)
    }
    v351
  }

  def matchEmptySequence(node: Node): EmptySeq = {
    val BindNode(v352, v353) = node
    val v354 = v352.id match {
      case 285 =>
        EmptySeq()(v353)
    }
    v354
  }

  def matchEnumTypeDef(node: Node): EnumTypeDef = {
    val BindNode(v355, v356) = node
    val v383 = v355.id match {
      case 181 =>
        val v357 = v356.asInstanceOf[SequenceNode].children.head
        val BindNode(v358, v359) = v357
        assert(v358.id == 117)
        val v360 = v356.asInstanceOf[SequenceNode].children(4)
        val BindNode(v361, v362) = v360
        assert(v361.id == 182)
        val BindNode(v363, v364) = v362
        val v382 = v363.id match {
          case 183 =>
            val BindNode(v365, v366) = v364
            assert(v365.id == 184)
            val v367 = v366.asInstanceOf[SequenceNode].children.head
            val BindNode(v368, v369) = v367
            assert(v368.id == 49)
            val v370 = v366.asInstanceOf[SequenceNode].children(1)
            val v371 = unrollRepeat0(v370).map { elem =>
              val BindNode(v372, v373) = elem
              assert(v372.id == 187)
              val BindNode(v374, v375) = v373
              val v381 = v374.id match {
                case 188 =>
                  val BindNode(v376, v377) = v375
                  assert(v376.id == 189)
                  val v378 = v377.asInstanceOf[SequenceNode].children(3)
                  val BindNode(v379, v380) = v378
                  assert(v379.id == 49)
                  matchId(v380)
              }
              v381
            }
            List(matchId(v369)) ++ v371
        }
        EnumTypeDef(matchEnumTypeName(v359), v382)(v356)
    }
    v383
  }

  def matchEnumTypeName(node: Node): EnumTypeName = {
    val BindNode(v384, v385) = node
    val v389 = v384.id match {
      case 118 =>
        val v386 = v385.asInstanceOf[SequenceNode].children(1)
        val BindNode(v387, v388) = v386
        assert(v387.id == 49)
        EnumTypeName(matchId(v388))(v385)
    }
    v389
  }

  def matchEnumValue(node: Node): AbstractEnumValue = {
    val BindNode(v390, v391) = node
    val v410 = v390.id match {
      case 399 =>
        val v392 = v391.asInstanceOf[SequenceNode].children.head
        val BindNode(v393, v394) = v392
        assert(v393.id == 400)
        val BindNode(v395, v396) = v394
        assert(v395.id == 401)
        val BindNode(v397, v398) = v396
        val v409 = v397.id match {
          case 402 =>
            val BindNode(v399, v400) = v398
            assert(v399.id == 403)
            val v401 = v400.asInstanceOf[SequenceNode].children.head
            val BindNode(v402, v403) = v401
            assert(v402.id == 404)
            matchCanonicalEnumValue(v403)
          case 408 =>
            val BindNode(v404, v405) = v398
            assert(v404.id == 409)
            val v406 = v405.asInstanceOf[SequenceNode].children.head
            val BindNode(v407, v408) = v406
            assert(v407.id == 410)
            matchShortenedEnumValue(v408)
        }
        v409
    }
    v410
  }

  def matchEnumValueName(node: Node): EnumValueName = {
    val BindNode(v411, v412) = node
    val v416 = v411.id match {
      case 407 =>
        val v413 = v412.asInstanceOf[SequenceNode].children.head
        val BindNode(v414, v415) = v413
        assert(v414.id == 49)
        EnumValueName(matchId(v415))(v412)
    }
    v416
  }

  def matchFuncCallOrConstructExpr(node: Node): FuncCallOrConstructExpr = {
    val BindNode(v417, v418) = node
    val v425 = v417.id match {
      case 367 =>
        val v419 = v418.asInstanceOf[SequenceNode].children.head
        val BindNode(v420, v421) = v419
        assert(v420.id == 368)
        val v422 = v418.asInstanceOf[SequenceNode].children(2)
        val BindNode(v423, v424) = v422
        assert(v423.id == 369)
        FuncCallOrConstructExpr(matchTypeOrFuncName(v421), matchCallParams(v424))(v418)
    }
    v425
  }

  def matchGrammar(node: Node): Grammar = {
    val BindNode(v426, v427) = node
    val v443 = v426.id match {
      case 3 =>
        val v428 = v427.asInstanceOf[SequenceNode].children(1)
        val BindNode(v429, v430) = v428
        assert(v429.id == 36)
        val v431 = v427.asInstanceOf[SequenceNode].children(2)
        val v432 = unrollRepeat0(v431).map { elem =>
          val BindNode(v433, v434) = elem
          assert(v433.id == 453)
          val BindNode(v435, v436) = v434
          val v442 = v435.id match {
            case 454 =>
              val BindNode(v437, v438) = v436
              assert(v437.id == 455)
              val v439 = v438.asInstanceOf[SequenceNode].children(1)
              val BindNode(v440, v441) = v439
              assert(v440.id == 36)
              matchDef(v441)
          }
          v442
        }
        Grammar(List(matchDef(v430)) ++ v432)(v427)
    }
    v443
  }

  def matchId(node: Node): String = {
    val BindNode(v444, v445) = node
    val v447 = v444.id match {
      case 50 =>
        val v446 = v445.asInstanceOf[SequenceNode].children.head
        v446.sourceText
    }
    v447
  }

  def matchIdNoKeyword(node: Node): String = {
    val BindNode(v448, v449) = node
    val v451 = v448.id match {
      case 47 =>
        val v450 = v449.asInstanceOf[SequenceNode].children.head
        v450.sourceText
    }
    v451
  }

  def matchInPlaceChoices(node: Node): InPlaceChoices = {
    val BindNode(v452, v453) = node
    val v469 = v452.id match {
      case 273 =>
        val v454 = v453.asInstanceOf[SequenceNode].children.head
        val BindNode(v455, v456) = v454
        assert(v455.id == 201)
        val v457 = v453.asInstanceOf[SequenceNode].children(1)
        val v458 = unrollRepeat0(v457).map { elem =>
          val BindNode(v459, v460) = elem
          assert(v459.id == 276)
          val BindNode(v461, v462) = v460
          val v468 = v461.id match {
            case 277 =>
              val BindNode(v463, v464) = v462
              assert(v463.id == 278)
              val v465 = v464.asInstanceOf[SequenceNode].children(3)
              val BindNode(v466, v467) = v465
              assert(v466.id == 201)
              matchSequence(v467)
          }
          v468
        }
        InPlaceChoices(List(matchSequence(v456)) ++ v458)(v453)
    }
    v469
  }

  def matchLHS(node: Node): LHS = {
    val BindNode(v470, v471) = node
    val v489 = v470.id match {
      case 41 =>
        val v472 = v471.asInstanceOf[SequenceNode].children.head
        val BindNode(v473, v474) = v472
        assert(v473.id == 42)
        val v475 = v471.asInstanceOf[SequenceNode].children(1)
        val BindNode(v476, v477) = v475
        assert(v476.id == 95)
        val BindNode(v478, v479) = v477
        val v488 = v478.id match {
          case 138 =>
            None
          case 96 =>
            val BindNode(v480, v481) = v479
            val v487 = v480.id match {
              case 97 =>
                val BindNode(v482, v483) = v481
                assert(v482.id == 98)
                val v484 = v483.asInstanceOf[SequenceNode].children(3)
                val BindNode(v485, v486) = v484
                assert(v485.id == 100)
                matchTypeDesc(v486)
            }
            Some(v487)
        }
        LHS(matchNonterminal(v474), v488)(v471)
    }
    v489
  }

  def matchLiteral(node: Node): Literal = {
    val BindNode(v490, v491) = node
    val v509 = v490.id match {
      case 90 =>
        NullLiteral()(v491)
      case 385 =>
        val v492 = v491.asInstanceOf[SequenceNode].children.head
        val BindNode(v493, v494) = v492
        assert(v493.id == 386)
        val BindNode(v495, v496) = v494
        val v501 = v495.id match {
          case 387 =>
            val BindNode(v497, v498) = v496
            assert(v497.id == 82)
            true
          case 388 =>
            val BindNode(v499, v500) = v496
            assert(v499.id == 86)
            false
        }
        BoolLiteral(v501)(v491)
      case 389 =>
        val v502 = v491.asInstanceOf[SequenceNode].children(1)
        val BindNode(v503, v504) = v502
        assert(v503.id == 390)
        CharLiteral(matchCharChar(v504))(v491)
      case 392 =>
        val v505 = v491.asInstanceOf[SequenceNode].children(1)
        val v506 = unrollRepeat0(v505).map { elem =>
          val BindNode(v507, v508) = elem
          assert(v507.id == 395)
          matchStrChar(v508)
        }
        StrLiteral(v506)(v491)
    }
    v509
  }

  def matchLongest(node: Node): Longest = {
    val BindNode(v510, v511) = node
    val v515 = v510.id match {
      case 282 =>
        val v512 = v511.asInstanceOf[SequenceNode].children(2)
        val BindNode(v513, v514) = v512
        assert(v513.id == 272)
        Longest(matchInPlaceChoices(v514))(v511)
    }
    v515
  }

  def matchNamedConstructExpr(node: Node): NamedConstructExpr = {
    val BindNode(v516, v517) = node
    val v538 = v516.id match {
      case 352 =>
        val v518 = v517.asInstanceOf[SequenceNode].children.head
        val BindNode(v519, v520) = v518
        assert(v519.id == 104)
        val v521 = v517.asInstanceOf[SequenceNode].children(3)
        val BindNode(v522, v523) = v521
        assert(v522.id == 353)
        val v524 = v517.asInstanceOf[SequenceNode].children(1)
        val BindNode(v525, v526) = v524
        assert(v525.id == 161)
        val BindNode(v527, v528) = v526
        val v537 = v527.id match {
          case 138 =>
            None
          case 162 =>
            val BindNode(v529, v530) = v528
            val v536 = v529.id match {
              case 163 =>
                val BindNode(v531, v532) = v530
                assert(v531.id == 164)
                val v533 = v532.asInstanceOf[SequenceNode].children(1)
                val BindNode(v534, v535) = v533
                assert(v534.id == 125)
                matchSuperTypes(v535)
            }
            Some(v536)
        }
        NamedConstructExpr(matchTypeName(v520), matchNamedConstructParams(v523), v537)(v517)
    }
    v538
  }

  def matchNamedConstructParams(node: Node): List[NamedParam] = {
    val BindNode(v539, v540) = node
    val v564 = v539.id match {
      case 354 =>
        val v541 = v540.asInstanceOf[SequenceNode].children(2)
        val BindNode(v542, v543) = v541
        assert(v542.id == 355)
        val BindNode(v544, v545) = v543
        val v563 = v544.id match {
          case 356 =>
            val BindNode(v546, v547) = v545
            assert(v546.id == 357)
            val v548 = v547.asInstanceOf[SequenceNode].children.head
            val BindNode(v549, v550) = v548
            assert(v549.id == 358)
            val v551 = v547.asInstanceOf[SequenceNode].children(1)
            val v552 = unrollRepeat0(v551).map { elem =>
              val BindNode(v553, v554) = elem
              assert(v553.id == 362)
              val BindNode(v555, v556) = v554
              val v562 = v555.id match {
                case 363 =>
                  val BindNode(v557, v558) = v556
                  assert(v557.id == 364)
                  val v559 = v558.asInstanceOf[SequenceNode].children(3)
                  val BindNode(v560, v561) = v559
                  assert(v560.id == 358)
                  matchNamedParam(v561)
              }
              v562
            }
            List(matchNamedParam(v550)) ++ v552
        }
        v563
    }
    v564
  }

  def matchNamedParam(node: Node): NamedParam = {
    val BindNode(v565, v566) = node
    val v587 = v565.id match {
      case 359 =>
        val v567 = v566.asInstanceOf[SequenceNode].children.head
        val BindNode(v568, v569) = v567
        assert(v568.id == 150)
        val v570 = v566.asInstanceOf[SequenceNode].children(1)
        val BindNode(v571, v572) = v570
        assert(v571.id == 95)
        val BindNode(v573, v574) = v572
        val v583 = v573.id match {
          case 138 =>
            None
          case 96 =>
            val BindNode(v575, v576) = v574
            val v582 = v575.id match {
              case 97 =>
                val BindNode(v577, v578) = v576
                assert(v577.id == 98)
                val v579 = v578.asInstanceOf[SequenceNode].children(3)
                val BindNode(v580, v581) = v579
                assert(v580.id == 100)
                matchTypeDesc(v581)
            }
            Some(v582)
        }
        val v584 = v566.asInstanceOf[SequenceNode].children(5)
        val BindNode(v585, v586) = v584
        assert(v585.id == 328)
        NamedParam(matchParamName(v569), v583, matchPExpr(v586))(v566)
    }
    v587
  }

  def matchNonNullTypeDesc(node: Node): NonNullTypeDesc = {
    val BindNode(v588, v589) = node
    val v608 = v588.id match {
      case 120 =>
        val v590 = v589.asInstanceOf[SequenceNode].children.head
        val BindNode(v591, v592) = v590
        assert(v591.id == 121)
        matchTypeDef(v592)
      case 105 =>
        val v593 = v589.asInstanceOf[SequenceNode].children(2)
        val BindNode(v594, v595) = v593
        assert(v594.id == 100)
        ArrayTypeDesc(matchTypeDesc(v595))(v589)
      case 108 =>
        val v596 = v589.asInstanceOf[SequenceNode].children.head
        val BindNode(v597, v598) = v596
        assert(v597.id == 109)
        matchValueType(v598)
      case 110 =>
        val v599 = v589.asInstanceOf[SequenceNode].children.head
        val BindNode(v600, v601) = v599
        assert(v600.id == 111)
        matchAnyType(v601)
      case 116 =>
        val v602 = v589.asInstanceOf[SequenceNode].children.head
        val BindNode(v603, v604) = v602
        assert(v603.id == 117)
        matchEnumTypeName(v604)
      case 103 =>
        val v605 = v589.asInstanceOf[SequenceNode].children.head
        val BindNode(v606, v607) = v605
        assert(v606.id == 104)
        matchTypeName(v607)
    }
    v608
  }

  def matchNonterminal(node: Node): Nonterminal = {
    val BindNode(v609, v610) = node
    val v614 = v609.id match {
      case 43 =>
        val v611 = v610.asInstanceOf[SequenceNode].children.head
        val BindNode(v612, v613) = v611
        assert(v612.id == 44)
        Nonterminal(matchNonterminalName(v613))(v610)
    }
    v614
  }

  def matchNonterminalName(node: Node): NonterminalName = {
    val BindNode(v615, v616) = node
    val v623 = v615.id match {
      case 45 =>
        val v617 = v616.asInstanceOf[SequenceNode].children.head
        val BindNode(v618, v619) = v617
        assert(v618.id == 46)
        NonterminalName(matchIdNoKeyword(v619))(v616)
      case 93 =>
        val v620 = v616.asInstanceOf[SequenceNode].children(1)
        val BindNode(v621, v622) = v620
        assert(v621.id == 49)
        NonterminalName(matchId(v622))(v616)
    }
    v623
  }

  def matchPExpr(node: Node): PExpr = {
    val BindNode(v624, v625) = node
    val v635 = v624.id match {
      case 329 =>
        val v626 = v625.asInstanceOf[SequenceNode].children.head
        val BindNode(v627, v628) = v626
        assert(v627.id == 330)
        val v629 = v625.asInstanceOf[SequenceNode].children(4)
        val BindNode(v630, v631) = v629
        assert(v630.id == 100)
        TypedPExpr(matchTernaryExpr(v628), matchTypeDesc(v631))(v625)
      case 439 =>
        val v632 = v625.asInstanceOf[SequenceNode].children.head
        val BindNode(v633, v634) = v632
        assert(v633.id == 330)
        matchTernaryExpr(v634)
    }
    v635
  }

  def matchPExprBlock(node: Node): ProcessorBlock = {
    val BindNode(v636, v637) = node
    val v641 = v636.id match {
      case 327 =>
        val v638 = v637.asInstanceOf[SequenceNode].children(2)
        val BindNode(v639, v640) = v638
        assert(v639.id == 328)
        ProcessorBlock(matchPExpr(v640))(v637)
    }
    v641
  }

  def matchParamName(node: Node): ParamName = {
    val BindNode(v642, v643) = node
    val v650 = v642.id match {
      case 45 =>
        val v644 = v643.asInstanceOf[SequenceNode].children.head
        val BindNode(v645, v646) = v644
        assert(v645.id == 46)
        ParamName(matchIdNoKeyword(v646))(v643)
      case 93 =>
        val v647 = v643.asInstanceOf[SequenceNode].children(1)
        val BindNode(v648, v649) = v647
        assert(v648.id == 49)
        ParamName(matchId(v649))(v643)
    }
    v650
  }

  def matchPostUnSymbol(node: Node): PostUnSymbol = {
    val BindNode(v651, v652) = node
    val v665 = v651.id match {
      case 217 =>
        val v653 = v652.asInstanceOf[SequenceNode].children.head
        val BindNode(v654, v655) = v653
        assert(v654.id == 216)
        Optional(matchPostUnSymbol(v655))(v652)
      case 218 =>
        val v656 = v652.asInstanceOf[SequenceNode].children.head
        val BindNode(v657, v658) = v656
        assert(v657.id == 216)
        RepeatFromZero(matchPostUnSymbol(v658))(v652)
      case 220 =>
        val v659 = v652.asInstanceOf[SequenceNode].children.head
        val BindNode(v660, v661) = v659
        assert(v660.id == 216)
        RepeatFromOne(matchPostUnSymbol(v661))(v652)
      case 222 =>
        val v662 = v652.asInstanceOf[SequenceNode].children.head
        val BindNode(v663, v664) = v662
        assert(v663.id == 223)
        matchAtomSymbol(v664)
    }
    v665
  }

  def matchPreUnSymbol(node: Node): PreUnSymbol = {
    val BindNode(v666, v667) = node
    val v677 = v666.id match {
      case 211 =>
        val v668 = v667.asInstanceOf[SequenceNode].children(2)
        val BindNode(v669, v670) = v668
        assert(v669.id == 210)
        FollowedBy(matchPreUnSymbol(v670))(v667)
      case 213 =>
        val v671 = v667.asInstanceOf[SequenceNode].children(2)
        val BindNode(v672, v673) = v671
        assert(v672.id == 210)
        NotFollowedBy(matchPreUnSymbol(v673))(v667)
      case 215 =>
        val v674 = v667.asInstanceOf[SequenceNode].children.head
        val BindNode(v675, v676) = v674
        assert(v675.id == 216)
        matchPostUnSymbol(v676)
    }
    v677
  }

  def matchPrefixNotExpr(node: Node): PrefixNotExpr = {
    val BindNode(v678, v679) = node
    val v686 = v678.id match {
      case 343 =>
        val v680 = v679.asInstanceOf[SequenceNode].children(2)
        val BindNode(v681, v682) = v680
        assert(v681.id == 342)
        PrefixOp(PreOp.NOT, matchPrefixNotExpr(v682))(v679)
      case 344 =>
        val v683 = v679.asInstanceOf[SequenceNode].children.head
        val BindNode(v684, v685) = v683
        assert(v684.id == 345)
        matchAtom(v685)
    }
    v686
  }

  def matchProcessor(node: Node): Processor = {
    val BindNode(v687, v688) = node
    val v695 = v687.id match {
      case 291 =>
        val v689 = v688.asInstanceOf[SequenceNode].children.head
        val BindNode(v690, v691) = v689
        assert(v690.id == 292)
        matchRef(v691)
      case 325 =>
        val v692 = v688.asInstanceOf[SequenceNode].children.head
        val BindNode(v693, v694) = v692
        assert(v693.id == 326)
        matchPExprBlock(v694)
    }
    v695
  }

  def matchRHS(node: Node): Sequence = {
    val BindNode(v696, v697) = node
    val v701 = v696.id match {
      case 200 =>
        val v698 = v697.asInstanceOf[SequenceNode].children.head
        val BindNode(v699, v700) = v698
        assert(v699.id == 201)
        matchSequence(v700)
    }
    v701
  }

  def matchRawRef(node: Node): RawRef = {
    val BindNode(v702, v703) = node
    val v713 = v702.id match {
      case 322 =>
        val v704 = v703.asInstanceOf[SequenceNode].children(2)
        val BindNode(v705, v706) = v704
        assert(v705.id == 307)
        val v707 = v703.asInstanceOf[SequenceNode].children(1)
        val BindNode(v708, v709) = v707
        assert(v708.id == 297)
        val BindNode(v710, v711) = v709
        val v712 = v710.id match {
          case 138 =>
            None
          case 298 =>
            Some(matchCondSymPath(v711))
        }
        RawRef(matchRefIdx(v706), v712)(v703)
    }
    v713
  }

  def matchRef(node: Node): Ref = {
    val BindNode(v714, v715) = node
    val v722 = v714.id match {
      case 293 =>
        val v716 = v715.asInstanceOf[SequenceNode].children.head
        val BindNode(v717, v718) = v716
        assert(v717.id == 294)
        matchValRef(v718)
      case 320 =>
        val v719 = v715.asInstanceOf[SequenceNode].children.head
        val BindNode(v720, v721) = v719
        assert(v720.id == 321)
        matchRawRef(v721)
    }
    v722
  }

  def matchRefIdx(node: Node): String = {
    val BindNode(v723, v724) = node
    val v726 = v723.id match {
      case 308 =>
        val v725 = v724.asInstanceOf[SequenceNode].children.head
        v725.sourceText
    }
    v726
  }

  def matchRule(node: Node): Rule = {
    val BindNode(v727, v728) = node
    val v755 = v727.id match {
      case 39 =>
        val v729 = v728.asInstanceOf[SequenceNode].children.head
        val BindNode(v730, v731) = v729
        assert(v730.id == 40)
        val v732 = v728.asInstanceOf[SequenceNode].children(4)
        val BindNode(v733, v734) = v732
        assert(v733.id == 196)
        val BindNode(v735, v736) = v734
        val v754 = v735.id match {
          case 197 =>
            val BindNode(v737, v738) = v736
            assert(v737.id == 198)
            val v739 = v738.asInstanceOf[SequenceNode].children.head
            val BindNode(v740, v741) = v739
            assert(v740.id == 199)
            val v742 = v738.asInstanceOf[SequenceNode].children(1)
            val v743 = unrollRepeat0(v742).map { elem =>
              val BindNode(v744, v745) = elem
              assert(v744.id == 448)
              val BindNode(v746, v747) = v745
              val v753 = v746.id match {
                case 449 =>
                  val BindNode(v748, v749) = v747
                  assert(v748.id == 450)
                  val v750 = v749.asInstanceOf[SequenceNode].children(3)
                  val BindNode(v751, v752) = v750
                  assert(v751.id == 199)
                  matchRHS(v752)
              }
              v753
            }
            List(matchRHS(v741)) ++ v743
        }
        Rule(matchLHS(v731), v754)(v728)
    }
    v755
  }

  def matchSequence(node: Node): Sequence = {
    val BindNode(v756, v757) = node
    val v773 = v756.id match {
      case 202 =>
        val v758 = v757.asInstanceOf[SequenceNode].children.head
        val BindNode(v759, v760) = v758
        assert(v759.id == 203)
        val v761 = v757.asInstanceOf[SequenceNode].children(1)
        val v762 = unrollRepeat0(v761).map { elem =>
          val BindNode(v763, v764) = elem
          assert(v763.id == 443)
          val BindNode(v765, v766) = v764
          val v772 = v765.id match {
            case 444 =>
              val BindNode(v767, v768) = v766
              assert(v767.id == 445)
              val v769 = v768.asInstanceOf[SequenceNode].children(1)
              val BindNode(v770, v771) = v769
              assert(v770.id == 203)
              matchElem(v771)
          }
          v772
        }
        Sequence(List(matchElem(v760)) ++ v762)(v757)
    }
    v773
  }

  def matchShortenedEnumValue(node: Node): ShortenedEnumValue = {
    val BindNode(v774, v775) = node
    val v779 = v774.id match {
      case 411 =>
        val v776 = v775.asInstanceOf[SequenceNode].children(1)
        val BindNode(v777, v778) = v776
        assert(v777.id == 406)
        ShortenedEnumValue(matchEnumValueName(v778))(v775)
    }
    v779
  }

  def matchStrChar(node: Node): StringChar = {
    val BindNode(v780, v781) = node
    val v785 = v780.id match {
      case 396 =>
        val v782 = v781.asInstanceOf[SequenceNode].children.head
        val BindNode(v783, v784) = v782
        assert(v783.id == 264)
        matchStringChar(v784)
    }
    v785
  }

  def matchStringChar(node: Node): StringChar = {
    val BindNode(v786, v787) = node
    val v799 = v786.id match {
      case 265 =>
        val v788 = v787.asInstanceOf[SequenceNode].children.head
        val BindNode(v789, v790) = v788
        assert(v789.id == 266)
        val BindNode(v791, v792) = v790
        assert(v791.id == 26)
        CharAsIs(v792.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v787)
      case 268 =>
        val v793 = v787.asInstanceOf[SequenceNode].children(1)
        val BindNode(v794, v795) = v793
        assert(v794.id == 269)
        CharEscaped(v795.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v787)
      case 234 =>
        val v796 = v787.asInstanceOf[SequenceNode].children.head
        val BindNode(v797, v798) = v796
        assert(v797.id == 235)
        matchUnicodeChar(v798)
    }
    v799
  }

  def matchStringSymbol(node: Node): StringSymbol = {
    val BindNode(v800, v801) = node
    val v806 = v800.id match {
      case 260 =>
        val v802 = v801.asInstanceOf[SequenceNode].children(1)
        val v803 = unrollRepeat0(v802).map { elem =>
          val BindNode(v804, v805) = elem
          assert(v804.id == 264)
          matchStringChar(v805)
        }
        StringSymbol(v803)(v801)
    }
    v806
  }

  def matchSubType(node: Node): SubType = {
    val BindNode(v807, v808) = node
    val v818 = v807.id match {
      case 103 =>
        val v809 = v808.asInstanceOf[SequenceNode].children.head
        val BindNode(v810, v811) = v809
        assert(v810.id == 104)
        matchTypeName(v811)
      case 122 =>
        val v812 = v808.asInstanceOf[SequenceNode].children.head
        val BindNode(v813, v814) = v812
        assert(v813.id == 123)
        matchClassDef(v814)
      case 158 =>
        val v815 = v808.asInstanceOf[SequenceNode].children.head
        val BindNode(v816, v817) = v815
        assert(v816.id == 159)
        matchSuperDef(v817)
    }
    v818
  }

  def matchSubTypes(node: Node): List[SubType] = {
    val BindNode(v819, v820) = node
    val v836 = v819.id match {
      case 171 =>
        val v821 = v820.asInstanceOf[SequenceNode].children.head
        val BindNode(v822, v823) = v821
        assert(v822.id == 172)
        val v824 = v820.asInstanceOf[SequenceNode].children(1)
        val v825 = unrollRepeat0(v824).map { elem =>
          val BindNode(v826, v827) = elem
          assert(v826.id == 175)
          val BindNode(v828, v829) = v827
          val v835 = v828.id match {
            case 176 =>
              val BindNode(v830, v831) = v829
              assert(v830.id == 177)
              val v832 = v831.asInstanceOf[SequenceNode].children(3)
              val BindNode(v833, v834) = v832
              assert(v833.id == 172)
              matchSubType(v834)
          }
          v835
        }
        List(matchSubType(v823)) ++ v825
    }
    v836
  }

  def matchSuperDef(node: Node): SuperDef = {
    val BindNode(v837, v838) = node
    val v870 = v837.id match {
      case 160 =>
        val v839 = v838.asInstanceOf[SequenceNode].children.head
        val BindNode(v840, v841) = v839
        assert(v840.id == 104)
        val v842 = v838.asInstanceOf[SequenceNode].children(4)
        val BindNode(v843, v844) = v842
        assert(v843.id == 166)
        val BindNode(v845, v846) = v844
        val v855 = v845.id match {
          case 138 =>
            None
          case 167 =>
            val BindNode(v847, v848) = v846
            val v854 = v847.id match {
              case 168 =>
                val BindNode(v849, v850) = v848
                assert(v849.id == 169)
                val v851 = v850.asInstanceOf[SequenceNode].children(1)
                val BindNode(v852, v853) = v851
                assert(v852.id == 170)
                matchSubTypes(v853)
            }
            Some(v854)
        }
        val v856 = v838.asInstanceOf[SequenceNode].children(1)
        val BindNode(v857, v858) = v856
        assert(v857.id == 161)
        val BindNode(v859, v860) = v858
        val v869 = v859.id match {
          case 138 =>
            None
          case 162 =>
            val BindNode(v861, v862) = v860
            val v868 = v861.id match {
              case 163 =>
                val BindNode(v863, v864) = v862
                assert(v863.id == 164)
                val v865 = v864.asInstanceOf[SequenceNode].children(1)
                val BindNode(v866, v867) = v865
                assert(v866.id == 125)
                matchSuperTypes(v867)
            }
            Some(v868)
        }
        SuperDef(matchTypeName(v841), v855, v869)(v838)
    }
    v870
  }

  def matchSuperTypes(node: Node): List[TypeName] = {
    val BindNode(v871, v872) = node
    val v900 = v871.id match {
      case 126 =>
        val v874 = v872.asInstanceOf[SequenceNode].children(2)
        val BindNode(v875, v876) = v874
        assert(v875.id == 128)
        val BindNode(v877, v878) = v876
        val v899 = v877.id match {
          case 138 =>
            None
          case 129 =>
            val BindNode(v879, v880) = v878
            val v898 = v879.id match {
              case 130 =>
                val BindNode(v881, v882) = v880
                assert(v881.id == 131)
                val v883 = v882.asInstanceOf[SequenceNode].children.head
                val BindNode(v884, v885) = v883
                assert(v884.id == 104)
                val v886 = v882.asInstanceOf[SequenceNode].children(1)
                val v887 = unrollRepeat0(v886).map { elem =>
                  val BindNode(v888, v889) = elem
                  assert(v888.id == 134)
                  val BindNode(v890, v891) = v889
                  val v897 = v890.id match {
                    case 135 =>
                      val BindNode(v892, v893) = v891
                      assert(v892.id == 136)
                      val v894 = v893.asInstanceOf[SequenceNode].children(3)
                      val BindNode(v895, v896) = v894
                      assert(v895.id == 104)
                      matchTypeName(v896)
                  }
                  v897
                }
                List(matchTypeName(v885)) ++ v887
            }
            Some(v898)
        }
        val v873 = v899
        if (v873.isDefined) v873.get else List()
    }
    v900
  }

  def matchSymbol(node: Node): Symbol = {
    val BindNode(v901, v902) = node
    val v906 = v901.id match {
      case 206 =>
        val v903 = v902.asInstanceOf[SequenceNode].children.head
        val BindNode(v904, v905) = v903
        assert(v904.id == 207)
        matchBinSymbol(v905)
    }
    v906
  }

  def matchTerminal(node: Node): Terminal = {
    val BindNode(v907, v908) = node
    val v912 = v907.id match {
      case 226 =>
        val v909 = v908.asInstanceOf[SequenceNode].children(1)
        val BindNode(v910, v911) = v909
        assert(v910.id == 228)
        matchTerminalChar(v911)
      case 238 =>
        AnyTerminal()(v908)
    }
    v912
  }

  def matchTerminalChar(node: Node): TerminalChar = {
    val BindNode(v913, v914) = node
    val v926 = v913.id match {
      case 229 =>
        val v915 = v914.asInstanceOf[SequenceNode].children.head
        val BindNode(v916, v917) = v915
        assert(v916.id == 230)
        val BindNode(v918, v919) = v917
        assert(v918.id == 26)
        CharAsIs(v919.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v914)
      case 232 =>
        val v920 = v914.asInstanceOf[SequenceNode].children(1)
        val BindNode(v921, v922) = v920
        assert(v921.id == 233)
        CharEscaped(v922.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v914)
      case 234 =>
        val v923 = v914.asInstanceOf[SequenceNode].children.head
        val BindNode(v924, v925) = v923
        assert(v924.id == 235)
        matchUnicodeChar(v925)
    }
    v926
  }

  def matchTerminalChoice(node: Node): TerminalChoice = {
    val BindNode(v927, v928) = node
    val v939 = v927.id match {
      case 242 =>
        val v929 = v928.asInstanceOf[SequenceNode].children(1)
        val BindNode(v930, v931) = v929
        assert(v930.id == 243)
        val v932 = v928.asInstanceOf[SequenceNode].children(2)
        val v933 = unrollRepeat1(v932).map { elem =>
          val BindNode(v934, v935) = elem
          assert(v934.id == 243)
          matchTerminalChoiceElem(v935)
        }
        TerminalChoice(List(matchTerminalChoiceElem(v931)) ++ v933)(v928)
      case 257 =>
        val v936 = v928.asInstanceOf[SequenceNode].children(1)
        val BindNode(v937, v938) = v936
        assert(v937.id == 252)
        TerminalChoice(List(matchTerminalChoiceRange(v938)))(v928)
    }
    v939
  }

  def matchTerminalChoiceChar(node: Node): TerminalChoiceChar = {
    val BindNode(v940, v941) = node
    val v953 = v940.id match {
      case 246 =>
        val v942 = v941.asInstanceOf[SequenceNode].children.head
        val BindNode(v943, v944) = v942
        assert(v943.id == 247)
        val BindNode(v945, v946) = v944
        assert(v945.id == 26)
        CharAsIs(v946.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v941)
      case 249 =>
        val v947 = v941.asInstanceOf[SequenceNode].children(1)
        val BindNode(v948, v949) = v947
        assert(v948.id == 250)
        CharEscaped(v949.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char)(v941)
      case 234 =>
        val v950 = v941.asInstanceOf[SequenceNode].children.head
        val BindNode(v951, v952) = v950
        assert(v951.id == 235)
        matchUnicodeChar(v952)
    }
    v953
  }

  def matchTerminalChoiceElem(node: Node): TerminalChoiceElem = {
    val BindNode(v954, v955) = node
    val v962 = v954.id match {
      case 244 =>
        val v956 = v955.asInstanceOf[SequenceNode].children.head
        val BindNode(v957, v958) = v956
        assert(v957.id == 245)
        matchTerminalChoiceChar(v958)
      case 251 =>
        val v959 = v955.asInstanceOf[SequenceNode].children.head
        val BindNode(v960, v961) = v959
        assert(v960.id == 252)
        matchTerminalChoiceRange(v961)
    }
    v962
  }

  def matchTerminalChoiceRange(node: Node): TerminalChoiceRange = {
    val BindNode(v963, v964) = node
    val v971 = v963.id match {
      case 253 =>
        val v965 = v964.asInstanceOf[SequenceNode].children.head
        val BindNode(v966, v967) = v965
        assert(v966.id == 245)
        val v968 = v964.asInstanceOf[SequenceNode].children(2)
        val BindNode(v969, v970) = v968
        assert(v969.id == 245)
        TerminalChoiceRange(matchTerminalChoiceChar(v967), matchTerminalChoiceChar(v970))(v964)
    }
    v971
  }

  def matchTernaryExpr(node: Node): TernaryExpr = {
    val BindNode(v972, v973) = node
    val v1006 = v972.id match {
      case 331 =>
        val v974 = v973.asInstanceOf[SequenceNode].children.head
        val BindNode(v975, v976) = v974
        assert(v975.id == 332)
        val v977 = v973.asInstanceOf[SequenceNode].children(4)
        val BindNode(v978, v979) = v977
        assert(v978.id == 436)
        val BindNode(v980, v981) = v979
        assert(v980.id == 437)
        val BindNode(v982, v983) = v981
        val v989 = v982.id match {
          case 438 =>
            val BindNode(v984, v985) = v983
            assert(v984.id == 439)
            val v986 = v985.asInstanceOf[SequenceNode].children.head
            val BindNode(v987, v988) = v986
            assert(v987.id == 330)
            matchTernaryExpr(v988)
        }
        val v990 = v973.asInstanceOf[SequenceNode].children(8)
        val BindNode(v991, v992) = v990
        assert(v991.id == 436)
        val BindNode(v993, v994) = v992
        assert(v993.id == 437)
        val BindNode(v995, v996) = v994
        val v1002 = v995.id match {
          case 438 =>
            val BindNode(v997, v998) = v996
            assert(v997.id == 439)
            val v999 = v998.asInstanceOf[SequenceNode].children.head
            val BindNode(v1000, v1001) = v999
            assert(v1000.id == 330)
            matchTernaryExpr(v1001)
        }
        TernaryOp(matchBoolOrExpr(v976), v989, v1002)(v973)
      case 440 =>
        val v1003 = v973.asInstanceOf[SequenceNode].children.head
        val BindNode(v1004, v1005) = v1003
        assert(v1004.id == 332)
        matchBoolOrExpr(v1005)
    }
    v1006
  }

  def matchTypeDef(node: Node): TypeDef = {
    val BindNode(v1007, v1008) = node
    val v1018 = v1007.id match {
      case 122 =>
        val v1009 = v1008.asInstanceOf[SequenceNode].children.head
        val BindNode(v1010, v1011) = v1009
        assert(v1010.id == 123)
        matchClassDef(v1011)
      case 158 =>
        val v1012 = v1008.asInstanceOf[SequenceNode].children.head
        val BindNode(v1013, v1014) = v1012
        assert(v1013.id == 159)
        matchSuperDef(v1014)
      case 179 =>
        val v1015 = v1008.asInstanceOf[SequenceNode].children.head
        val BindNode(v1016, v1017) = v1015
        assert(v1016.id == 180)
        matchEnumTypeDef(v1017)
    }
    v1018
  }

  def matchTypeDesc(node: Node): TypeDesc = {
    val BindNode(v1019, v1020) = node
    val v1038 = v1019.id match {
      case 101 =>
        val v1021 = v1020.asInstanceOf[SequenceNode].children.head
        val BindNode(v1022, v1023) = v1021
        assert(v1022.id == 102)
        val v1024 = v1020.asInstanceOf[SequenceNode].children(1)
        val BindNode(v1025, v1026) = v1024
        assert(v1025.id == 190)
        val BindNode(v1027, v1028) = v1026
        val v1037 = v1027.id match {
          case 138 =>
            None
          case 191 =>
            val BindNode(v1029, v1030) = v1028
            val v1036 = v1029.id match {
              case 192 =>
                val BindNode(v1031, v1032) = v1030
                assert(v1031.id == 193)
                val v1033 = v1032.asInstanceOf[SequenceNode].children(1)
                val BindNode(v1034, v1035) = v1033
                assert(v1034.id == 194)
                v1035.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char
            }
            Some(v1036)
        }
        TypeDesc(matchNonNullTypeDesc(v1023), v1037.isDefined)(v1020)
    }
    v1038
  }

  def matchTypeName(node: Node): TypeName = {
    val BindNode(v1039, v1040) = node
    val v1047 = v1039.id match {
      case 45 =>
        val v1041 = v1040.asInstanceOf[SequenceNode].children.head
        val BindNode(v1042, v1043) = v1041
        assert(v1042.id == 46)
        TypeName(matchIdNoKeyword(v1043))(v1040)
      case 93 =>
        val v1044 = v1040.asInstanceOf[SequenceNode].children(1)
        val BindNode(v1045, v1046) = v1044
        assert(v1045.id == 49)
        TypeName(matchId(v1046))(v1040)
    }
    v1047
  }

  def matchTypeOrFuncName(node: Node): TypeOrFuncName = {
    val BindNode(v1048, v1049) = node
    val v1056 = v1048.id match {
      case 45 =>
        val v1050 = v1049.asInstanceOf[SequenceNode].children.head
        val BindNode(v1051, v1052) = v1050
        assert(v1051.id == 46)
        TypeOrFuncName(matchIdNoKeyword(v1052))(v1049)
      case 93 =>
        val v1053 = v1049.asInstanceOf[SequenceNode].children(1)
        val BindNode(v1054, v1055) = v1053
        assert(v1054.id == 49)
        TypeOrFuncName(matchId(v1055))(v1049)
    }
    v1056
  }

  def matchUnicodeChar(node: Node): CharUnicode = {
    val BindNode(v1057, v1058) = node
    val v1071 = v1057.id match {
      case 236 =>
        val v1059 = v1058.asInstanceOf[SequenceNode].children(2)
        val BindNode(v1060, v1061) = v1059
        assert(v1060.id == 237)
        val v1062 = v1058.asInstanceOf[SequenceNode].children(3)
        val BindNode(v1063, v1064) = v1062
        assert(v1063.id == 237)
        val v1065 = v1058.asInstanceOf[SequenceNode].children(4)
        val BindNode(v1066, v1067) = v1065
        assert(v1066.id == 237)
        val v1068 = v1058.asInstanceOf[SequenceNode].children(5)
        val BindNode(v1069, v1070) = v1068
        assert(v1069.id == 237)
        CharUnicode(List(v1061.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char, v1064.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char, v1067.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char, v1070.asInstanceOf[TerminalNode].input.asInstanceOf[Inputs.Character].char))(v1058)
    }
    v1071
  }

  def matchValRef(node: Node): ValRef = {
    val BindNode(v1072, v1073) = node
    val v1083 = v1072.id match {
      case 295 =>
        val v1074 = v1073.asInstanceOf[SequenceNode].children(2)
        val BindNode(v1075, v1076) = v1074
        assert(v1075.id == 307)
        val v1077 = v1073.asInstanceOf[SequenceNode].children(1)
        val BindNode(v1078, v1079) = v1077
        assert(v1078.id == 297)
        val BindNode(v1080, v1081) = v1079
        val v1082 = v1080.id match {
          case 138 =>
            None
          case 298 =>
            Some(matchCondSymPath(v1081))
        }
        ValRef(matchRefIdx(v1076), v1082)(v1073)
    }
    v1083
  }

  def matchValueType(node: Node): ValueType = {
    val BindNode(v1084, v1085) = node
    val v1086 = v1084.id match {
      case 60 =>
        BooleanType()(v1085)
      case 69 =>
        CharType()(v1085)
      case 75 =>
        StringType()(v1085)
    }
    v1086
  }

  def matchStart(node: Node): Grammar = {
    val BindNode(start, BindNode(_, body)) = node
    assert(start.id == 1)
    matchGrammar(body)
  }

  val naiveParser = new NaiveParser(ngrammar)

  def parse(text: String): Either[Parser.NaiveContext, ParsingErrors.ParsingError] =
    naiveParser.parse(text)

  def parseAst(text: String): Either[Grammar, ParsingErrors.ParsingError] =
    ParseTreeUtil.parseAst(naiveParser, text, matchStart)

  def main(args: Array[String]): Unit = {
    println(parseAst("A = B C 'd' 'e'*"))
  }
}
