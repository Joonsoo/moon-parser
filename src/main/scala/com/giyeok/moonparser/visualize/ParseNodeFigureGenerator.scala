package com.giyeok.moonparser.visualize

import com.giyeok.moonparser.ParseTree.ParseNode
import com.giyeok.moonparser.Symbols._
import com.giyeok.moonparser.ParseTree.TreePrintableParseNode
import com.giyeok.moonparser.visualize.FigureGenerator.Appearance

class ParseNodeFigureGenerator[Fig](g: FigureGenerator.Generator[Fig], ap: FigureGenerator.Appearances[Fig]) {
    import com.giyeok.moonparser.ParseTree._
    import com.giyeok.moonparser.visualize.FigureGenerator.Spacing

    val symbolFigureGenerator = new SymbolFigureGenerator(g, ap)

    def parseNodeHFig(n: ParseNode[Symbol]): Fig =
        parseNodeHFig(n: ParseNode[Symbol], false, false)

    def parseNodeHFig(n: ParseNode[Symbol], renderJoin: Boolean, renderWS: Boolean): Fig = {
        parseNodeFig(ap.hSymbolBorder, g.verticalFig _, g.horizontalFig _, renderJoin: Boolean, renderWS: Boolean)(n)
    }

    def parseNodeVFig(n: ParseNode[Symbol]): Fig =
        parseNodeVFig(n: ParseNode[Symbol], false, false)

    def parseNodeVFig(n: ParseNode[Symbol], renderJoin: Boolean, renderWS: Boolean): Fig = {
        parseNodeFig(ap.vSymbolBorder, g.horizontalFig _, g.verticalFig _, renderJoin: Boolean, renderWS: Boolean)(n)
    }

    private def parseNodeFig(symbolBorder: FigureGenerator.Appearance[Fig], vfig: (Spacing.Value, Seq[Fig]) => Fig, hfig: (Spacing.Value, Seq[Fig]) => Fig, renderJoin: Boolean, renderWS: Boolean)(n: ParseNode[Symbol]): Fig = {
        def parseNodeFig(n: ParseNode[Symbol]): Fig = n match {
            case ParsedEmpty(sym) =>
                vfig(Spacing.Small, Seq(
                    g.textFig("", ap.default),
                    symbolFigureGenerator.symbolFig(sym)))
            case ParsedTerminal(sym, child) =>
                vfig(Spacing.Small, Seq(
                    g.textFig(child.toCleanString, ap.input),
                    symbolFigureGenerator.symbolFig(sym)))
            case ParsedSymbol(sym, body) =>
                vfig(Spacing.Small, Seq(
                    symbolBorder.applyToFigure(parseNodeFig(body)),
                    symbolFigureGenerator.symbolFig(sym)))
            case ParsedSymbolsSeq(sym, body, bodyWS) =>
                val seq: Seq[Fig] = if (renderWS && bodyWS.isDefined) {
                    val (bws, idx0) = bodyWS.get
                    val idx = if (idx0.last == bws.size - 1) idx0 else (idx0 :+ bws.size)
                    (idx.foldLeft(0, Seq[Fig]()) { (m, idx) =>
                        val (lastIdx, seq) = m
                        val wsFigs = (lastIdx until idx) map { wsIdx => ap.wsBorder.applyToFigure(parseNodeFig(bws(wsIdx))) }
                        val symFig = symbolBorder.applyToFigure(parseNodeFig(bws(idx)))
                        val newSeq = (seq ++ wsFigs) :+ symFig
                        (idx + 1, newSeq)
                    })._2
                } else {
                    body map { b => symbolBorder.applyToFigure(parseNodeFig(b)) }
                }
                vfig(Spacing.Small, Seq(
                    hfig(Spacing.Medium, seq),
                    symbolFigureGenerator.symbolFig(sym)))
            case ParsedSymbolJoin(sym, body, join) =>
                var content = Seq(symbolBorder.applyToFigure(parseNodeFig(body)))
                if (renderJoin) {
                    content :+= ap.joinHighlightBorder.applyToFigure(hfig(Spacing.Small, Seq(g.textFig("&", ap.default), parseNodeFig(join))))
                }
                content :+= symbolFigureGenerator.symbolFig(sym)
                vfig(Spacing.Small, content)
        }
        parseNodeFig(n)
    }
}
