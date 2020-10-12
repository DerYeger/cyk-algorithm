package eu.yeger.cyk

import eu.yeger.cyk.model.result
import eu.yeger.cyk.parser.grammar
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunningCYKTests {

    @Test
    fun verifyThatTheRunningCYKAlgorithmDetectsWordOfLanguage() {
        val states = runningCYK("she eats a fish with a fork") {
            grammar("S") {
                """
                    S -> NP VP
                    VP -> VP PP
                    VP -> V NP
                    VP -> eats
                    PP -> P NP
                    NP -> Det N
                    NP -> she
                    V -> eats
                    P -> with
                    N -> fish
                    N -> fork
                    Det -> a
                """.trimIndent()
            }
        }.getOrElse { error(it) }
        assertTrue(states.last().cykModel.result)
    }

    @Test
    fun verifyThatTheRunningCYKAlgorithmDetectsWordIsNotPartOfLanguage() {
        val states = runningCYK("she eats the fish with the fork") {
            grammar("S") {
                """
                    S -> NP VP
                    VP -> VP PP
                    VP -> V NP
                    VP -> eats
                    PP -> P NP
                    NP -> Det N
                    NP -> she
                    V -> eats
                    P -> with
                    N -> fish
                    N -> fork
                    Det -> a
                """.trimIndent()
            }
        }.getOrElse { error(it) }
        assertFalse(states.last().cykModel.result)
    }

    @Test
    fun verifyThatTheRunningCYKAlgorithmDetectsEmptyWords() {
        val states = runningCYK("") {
            grammar("S", includeEmptyProductionRule = true) { "" }
        }.getOrElse { error(it) }
        assertTrue(states.last().cykModel.result)
    }

    @Test
    fun verifyThatTheRunningCYKAlgorithmDoesntDetectsEmptyWordsIncorrectlyExplicitly() {
        val states = runningCYK("") {
            grammar("S", includeEmptyProductionRule = false) { "" }
        }.getOrElse { error(it) }
        assertFalse(states.last().cykModel.result)
    }

    @Test
    fun verifyThatTheRunningCYKAlgorithmDoesntDetectsEmptyWordsIncorrectlyImplicitly() {
        val states = runningCYK("") {
            grammar("S") { "" }
        }.getOrElse { error(it) }
        assertFalse(states.last().cykModel.result)
    }
}
