package com.giyeok.jparser.metalang3a

import com.giyeok.jparser.Symbols
import com.giyeok.jparser.metalang2.generated.MetaGrammar3Ast
import com.giyeok.jparser.metalang2.generated.MetaGrammar3Ast.{CharUnicode, StringChar, Terminal, TerminalChoiceElem}
import com.giyeok.jparser.metalang3.symbols.Escapes

object TerminalUtil {

    def charEscapedToChar(charEscaped: MetaGrammar3Ast.CharEscaped): Char = charEscaped.escapeCode.sourceText match {
        case "\'" => '\''
        case "\\" => '\\'
        case "b" => '\b'
        case "n" => '\n'
        case "r" => '\r'
        case "t" => '\t'
    }

    def charUnicodeToChar(charUnicode: CharUnicode): Char = {
        val code = charUnicode.code
        assert(code.size == 4)
        Integer.parseInt(s"${code(0).sourceText}${code(1).sourceText}${code(2).sourceText}${code(3).sourceText}", 16).toChar
    }

    def terminalCharToChar(c: MetaGrammar3Ast.TerminalChar): Char = c match {
        case MetaGrammar3Ast.CharAsIs(astNode, value) => value.sourceText.head
        case escaped: MetaGrammar3Ast.CharEscaped => charEscapedToChar(escaped)
        case unicode: MetaGrammar3Ast.CharUnicode => charUnicodeToChar(unicode)
    }

    def terminalChoiceCharToChar(c: MetaGrammar3Ast.TerminalChoiceChar): Char = c match {
        case MetaGrammar3Ast.CharAsIs(astNode, value) => value.sourceText.head
        case escaped: MetaGrammar3Ast.CharEscaped => charEscapedToChar(escaped)
        case unicode: MetaGrammar3Ast.CharUnicode => charUnicodeToChar(unicode)
    }

    def terminalToSymbol(terminal: Terminal): Symbols.Terminal = terminal match {
        case MetaGrammar3Ast.AnyTerminal(astNode) => Symbols.AnyChar
        case char: MetaGrammar3Ast.TerminalChar => Symbols.ExactChar(terminalCharToChar(char))
    }

    def terminalChoicesToSymbol(choices: List[TerminalChoiceElem]): Symbols.Terminal = {
        val chars = choices.flatMap {
            case MetaGrammar3Ast.TerminalChoiceRange(astNode, start, end) =>
                terminalChoiceCharToChar(start) to terminalChoiceCharToChar(end)
            case char: MetaGrammar3Ast.TerminalChoiceChar =>
                Set(terminalChoiceCharToChar(char))
        }.toSet
        Symbols.Chars(chars)
    }

    def stringCharToChar(stringChar: StringChar): Char = stringChar match {
        case MetaGrammar3Ast.CharAsIs(astNode, value) => value.sourceText.head
        case escaped: MetaGrammar3Ast.CharEscaped => charEscapedToChar(escaped)
        case unicode: MetaGrammar3Ast.CharUnicode => charUnicodeToChar(unicode)
    }

    def stringCharsToString(chars: List[StringChar]): String = chars.map(Escapes.stringCharToChar).mkString
}