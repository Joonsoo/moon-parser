package com.giyeok.jparser.parsergen

import com.giyeok.jparser.Inputs.{CharacterTermGroupDesc, CharsGroup, CharsGrouping, TermGroupDesc}
import com.giyeok.jparser.examples.ExpressionGrammars
import com.giyeok.jparser.nparser.NGrammar
import com.giyeok.jparser.parsergen.SimpleGen._
import com.giyeok.jparser.study.parsergen.AKernel

object SimpleGen {

    sealed trait Action

    case class Append(appendNodeType: Int, pendingFinish: Boolean = false) extends Action

    case class Replace(appendNodeType: Int) extends Action

    case class InReplaceAndAppend(inReplaceNodeType: Int, appendNodeType: Int, pendingFinish: Boolean) extends Action

    case object Finish extends Action

}

// grammar and nodes are debugging purpose
class SimpleGen(val grammar: NGrammar,
                val nodes: Map[Int, Set[AKernel]],
                val startNodeId: Int,
                val termActions: Map[(Int, CharacterTermGroupDesc), Action],
                // 엣지가 finish되면 새로 붙어야 하는 node
                val impliedNodes: Map[(Int, Int), Option[(Int, Boolean)]]
               ) {
    def genJava(pkgName: String, className: String): String = {
        val impliedNodesIfStack = ((impliedNodes.toList.sortBy { p => p._1 } map { impliedNode =>
            val impliedEdge = impliedNode._1
            val firstLine = s"if (prevNodeType == ${impliedEdge._1} && lastNodeType == ${impliedEdge._2}) {\n"
            val lastLine = "}"
            val body = impliedNode._2 match {
                case Some((implied, pendingFinish)) =>
                    s"""last = new Node($implied, last.parent);
                       |pendingFinish = $pendingFinish;
                     """.stripMargin
                case None =>
                    """last = last.parent;
                      |finish();
                    """.stripMargin
            }
            firstLine + body + lastLine
        }) :+
            s"""throw new RuntimeException("Unknown edge, " + prevNodeType + " -> " + lastNodeType + ", " + nodeDesc(prevNodeType) + " -> " + nodeDesc(lastNodeType));""") mkString " else "

        def javaChar(c: Char) = c

        def javaString(str: String) = str

        def charsToCondition(chars: Set[Char], varName: String): String =
            chars.groups map { group =>
                if (group._1 == group._2) s"($varName == '${javaChar(group._1)}')"
                else s"('${javaChar(group._1)}' <= $varName && $varName <= '${javaChar(group._2)}')"
            } mkString " || "

        def charGroupToCondition(charsGroup: CharsGroup, varName: String): String =
            charsToCondition(charsGroup.chars, varName)

        implicit def termGroupOrdering[A <: TermGroupDesc]: Ordering[A] = (x: A, y: A) => {
            (x, y) match {
                case (xx: CharsGroup, yy: CharsGroup) =>
                    xx.chars.min - yy.chars.min
            }
        }

        val nodeTermActions = (termActions groupBy { p => p._1._1 }).toList.sortBy { p => p._1 } map { p =>
            p._1 -> (p._2 map { pp => pp._1._2 -> pp._2 }).toList.sortBy { p3 => p3._1 }
        }

        val proceed1Stack = nodeTermActions map { nodeActions =>
            val (nodeTypeId, actions) = nodeActions
            val firstLine = s"case $nodeTypeId:"
            val body = actions map { termAction =>
                val (charsGroup, action) = termAction
                val firstLine = s"if (${charGroupToCondition(charsGroup.asInstanceOf[CharsGroup], "next")}) {"
                val body = action match {
                    case Append(appendNodeType, pendingFinish) => s"append($appendNodeType, $pendingFinish);"
                    case Replace(replaceNodeType) => s"replace($replaceNodeType);"
                    case InReplaceAndAppend(inReplaceNodeType, appendNodeType, pendingFinish) => s"inreplaceAppend($inReplaceNodeType, $appendNodeType, $pendingFinish);"
                    case Finish => "finish();"
                }
                val lastLine = "return true;\n}"
                firstLine + "\n" + body + "\n" + lastLine
            } mkString " else "
            val lastLine = "break;"
            firstLine + body + lastLine
        } mkString "\n"

        val canHandleStack = nodeTermActions map { nodeActions =>
            val (nodeTypeId, termActions) = nodeActions
            val condition = charsToCondition((termActions flatMap { p => p._1.asInstanceOf[CharsGroup].chars }).toSet, "c")
            s"case $nodeTypeId: return $condition;"
        } mkString "\n"

        val nodeDescriptionStack = nodes map { node =>
            val description = node._2 map { k => javaString(k.toReadableString(grammar, ".")) } mkString ","
            s"""case ${node._1}: return "{${javaString(description)}}";"""
        } mkString "\n"

        s"""package $pkgName;
           |
           |public class $className {
           |  static class Node {
           |    public final int nodeTypeId;
           |    public final Node parent;
           |
           |    public Node(int nodeTypeId, Node parent) {
           |      this.nodeTypeId = nodeTypeId;
           |      this.parent = parent;
           |    }
           |  }
           |
           |  private int location;
           |  private Node last;
           |  private boolean pendingFinish;
           |
           |  public $className() {
           |    last = new Node($startNodeId, null);
           |  }
           |
           |  private boolean canHandle(int nodeTypeId, char c) {
           |    switch (nodeTypeId) {
           |    $canHandleStack
           |    }
           |    throw new RuntimeException("Unknown nodeTypeId=" + nodeTypeId);
           |  }
           |
           |  private void append(int newNodeType, boolean pendingFinish) {
           |    last = new Node(newNodeType, last);
           |    this.pendingFinish = pendingFinish;
           |  }
           |
           |  private void replace(int newNodeType) {
           |    last = new Node(newNodeType, last.parent);
           |    this.pendingFinish = false;
           |  }
           |
           |  private void inreplaceAppend(int replaceNodeType, int appendNodeType, boolean pendingFinish) {
           |    last = new Node(appendNodeType, new Node(replaceNodeType, last.parent));
           |    this.pendingFinish = pendingFinish;
           |  }
           |
           |  private void finish() {
           |    System.out.println(nodeString() + " " + nodeDescString());
           |    int prevNodeType = last.parent.nodeTypeId, lastNodeType = last.nodeTypeId;
           |
           |    $impliedNodesIfStack
           |  }
           |
           |  private boolean tryFinishable(char next) {
           |    if (pendingFinish) {
           |      while (pendingFinish) {
           |        last = last.parent;
           |        finish();
           |        if (canHandle(last.nodeTypeId, next)) {
           |          return proceed1(next);
           |        }
           |      }
           |      return proceed1(next);
           |    } else {
           |      return false;
           |    }
           |  }
           |
           |  private boolean proceed1(char next) {
           |    switch (last.nodeTypeId) {
           |      $proceed1Stack
           |    }
           |    return tryFinishable(next);
           |  }
           |
           |  private String nodeString(Node node) {
           |    if (node.parent == null) return "" + node.nodeTypeId;
           |    else return nodeString(node.parent) + " " + node.nodeTypeId;
           |  }
           |
           |  public String nodeString() {
           |    return nodeString(last);
           |  }
           |
           |  public String nodeDesc(int nodeTypeId) {
           |    switch (nodeTypeId) {
           |      $nodeDescriptionStack
           |    }
           |    throw new RuntimeException("Unknown nodeTypeId=" + nodeTypeId);
           |  }
           |
           |  private String nodeDescString(Node node) {
           |    if (node.parent == null) return "" + nodeDesc(node.nodeTypeId);
           |    else return nodeDescString(node.parent) + " " + nodeDesc(node.nodeTypeId);
           |  }
           |
           |  public String nodeDescString() {
           |    return nodeDescString(last);
           |  }
           |
           |  public boolean proceed(char next) {
           |    location += 1;
           |    return proceed1(next);
           |  }
           |
           |  public boolean proceed(String next) {
           |    for (int i = 0; i < next.length(); i++) {
           |      System.out.println(nodeString() + " " + nodeDescString());
           |      System.out.println(i + " " + next.charAt(i));
           |      if (!proceed(next.charAt(i))) {
           |        return false;
           |      }
           |    }
           |    return true;
           |  }
           |}
        """.stripMargin
    }
}

object SimpleGenMain {
    def charsGroup(c: Char): CharacterTermGroupDesc =
        CharsGroup(Set(), Set(), Set(c))

    def charsGroup(start: Char, end: Char): CharacterTermGroupDesc =
        CharsGroup(Set(), Set(), (start to end).toSet)

    def main(args: Array[String]): Unit = {
        val grammar = NGrammar.fromGrammar(ExpressionGrammars.simple)
        val nodes: Map[Int, Set[AKernel]] = Map(
            1 -> Set(AKernel(1, 0)),
            2 -> Set(AKernel(16, 1), AKernel(18, 1)),
            3 -> Set(AKernel(7, 1), AKernel(16, 1), AKernel(18, 1)),
            4 -> Set(AKernel(13, 1)),
            5 -> Set(AKernel(11, 1)),
            6 -> Set(AKernel(16, 2)),
            7 -> Set(AKernel(18, 2)),
            8 -> Set(AKernel(7, 1)),
            9 -> Set(AKernel(16, 1)),
            10 -> Set(AKernel(7, 1), AKernel(16, 1)),
            11 -> Set(AKernel(18, 1)),
            12 -> Set(AKernel(13, 2)),
            13 -> Set(AKernel(11, 1), AKernel(16, 1)))
        val termActions: Map[(Int, CharacterTermGroupDesc), Action] = Map(
            (1, charsGroup('0')) -> Append(2, pendingFinish = true),
            (1, charsGroup('1', '9')) -> Append(3, pendingFinish = true),
            (1, charsGroup('(')) -> Append(4),
            (2, charsGroup('*')) -> Replace(6),
            (2, charsGroup('+')) -> Replace(7),
            (3, charsGroup('0', '9')) -> InReplaceAndAppend(8, 5, pendingFinish = true),
            (3, charsGroup('*')) -> Replace(6),
            (3, charsGroup('+')) -> Replace(7),
            (4, charsGroup('0')) -> Append(2),
            (4, charsGroup('1', '9')) -> Append(3),
            (4, charsGroup('(')) -> Append(4),
            (5, charsGroup('0', '9')) -> Finish,
            (6, charsGroup('0')) -> Finish,
            (6, charsGroup('1', '9')) -> Append(8, pendingFinish = true),
            (6, charsGroup('(')) -> Append(4),
            (7, charsGroup('0')) -> Append(9, pendingFinish = true),
            (7, charsGroup('1', '9')) -> Append(10, pendingFinish = true),
            (7, charsGroup('(')) -> Append(4),
            (8, charsGroup('0', '9')) -> Append(5, pendingFinish = true),
            (9, charsGroup('*')) -> Replace(6),
            (10, charsGroup('0', '9')) -> InReplaceAndAppend(8, 5, pendingFinish = true),
            (10, charsGroup('*')) -> Replace(6),
            (11, charsGroup('+')) -> Replace(7),
            (12, charsGroup(')')) -> Finish,
            (13, charsGroup('*')) -> Replace(6),
            (13, charsGroup('0', '9')) -> Finish
        )
        val impliedNodes: Map[(Int, Int), Option[(Int, Boolean)]] = Map(
            (1, 6) -> Some(2, true),
            (1, 7) -> Some(2, true),
            (1, 8) -> Some(3, true),
            (1, 12) -> Some(2, true),
            (4, 6) -> Some(2, true),
            (4, 7) -> Some(11, true),
            (4, 11) -> None,
            (4, 8) -> Some(3, true),
            (6, 8) -> None,
            (7, 10) -> None,
            (7, 12) -> Some(9, true),
            (7, 6) -> Some(9, true),
            (1, 4) -> Some(12, false),
            (4, 4) -> Some(12, false),
            (4, 12) -> Some(2, true),
            (6, 4) -> Some(12, false),
            (6, 12) -> None,
            (7, 4) -> Some(12, false),
            (7, 5) -> Some(13, true),
            (7, 8) -> Some(9, true),
            (8, 5) -> Some(5, true),
            (7, 9) -> Some(9, false)
        )
        val rule = new SimpleGen(grammar, nodes, 1, termActions, impliedNodes)
        println(rule.genJava("com.giyeok.jparser.parsergen.generated", "ExprGrammarSimpleParser"))
    }
}
