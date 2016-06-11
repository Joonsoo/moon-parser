package com.giyeok.jparser.tests

import com.giyeok.jparser.Inputs
import com.giyeok.jparser.Grammar
import com.giyeok.jparser.NewParser
import com.giyeok.jparser.DerivationSliceFunc
import com.giyeok.jparser.ParseForestFunc

trait Samples {
    val correctSampleInputs: Set[Inputs.ConcreteSource]
    val incorrectSampleInputs: Set[Inputs.ConcreteSource]
}

trait StringSamples extends Samples {
    val correctSamples: Set[String]
    val incorrectSamples: Set[String]

    lazy val correctSampleInputs: Set[Inputs.ConcreteSource] = correctSamples map { Inputs.fromString _ }
    lazy val incorrectSampleInputs: Set[Inputs.ConcreteSource] = incorrectSamples map { Inputs.fromString _ }
}

trait AmbiguousSamples extends Samples {
    val ambiguousSamples: Set[String]
    lazy val ambiguousSampleInputs: Set[Inputs.ConcreteSource] = ambiguousSamples map { Inputs.fromString _ }
}

trait GrammarTestCases extends Samples {
    val grammar: Grammar

    lazy val parser = {
        val dgraph = new DerivationSliceFunc(grammar, ParseForestFunc)
        // println(s"DerivationSliceFunc(${grammar.name}) created")
        val parser = new NewParser(grammar, ParseForestFunc, dgraph)
        // println(s"Parser(${grammar.name}) created")
        parser
    }
}

trait PreprocessedParser extends GrammarTestCases {
    override lazy val parser = {
        val dfunc = new DerivationSliceFunc(grammar, ParseForestFunc)
        val startTime = System.currentTimeMillis()
        println("Preprocess begins")
        dfunc.preprocess
        println(s"Preprocess done in ${System.currentTimeMillis() - startTime} ms")
        new NewParser(grammar, ParseForestFunc, dfunc)
    }
}