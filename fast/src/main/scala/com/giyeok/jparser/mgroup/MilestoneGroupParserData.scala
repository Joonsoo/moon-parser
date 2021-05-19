package com.giyeok.jparser.mgroup

import com.giyeok.jparser.Inputs.TermGroupDesc
import com.giyeok.jparser.NGrammar
import com.giyeok.jparser.fast.{GraphNoIndex, KernelTemplate, TasksSummary}
import com.giyeok.jparser.nparser.AcceptCondition.AcceptCondition

case class MilestoneGroup(milestones: Set[KernelTemplate])

case class MilestoneGroupParserData(grammar: NGrammar,
                                    byStart: TasksSummary,
                                    milestoneGroups: Map[Int, MilestoneGroup],
                                    termActions: Map[Int, List[(TermGroupDesc, ParsingAction)]],
                                    edgeProgressActions: Map[(Int, Int), ParsingAction],
                                    derivedGraph: Map[Int, GraphNoIndex])

case class ParsingAction(replaceTo: Int,
                         appendingMilestones: List[AppendingMilestone],
                         tasksSummary: TasksSummary,
                         startNodeProgressConditions: Map[KernelTemplate, AcceptCondition],
                         graphBetween: GraphNoIndex)

case class AppendingMilestone(appendingGroup: Int,
                              acceptCondition: AcceptCondition,
                              dependents: List[(Int, Int, AcceptCondition)])
