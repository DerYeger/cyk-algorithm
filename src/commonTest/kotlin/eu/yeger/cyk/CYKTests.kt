package eu.yeger.cyk

import eu.yeger.cyk.model.result
import eu.yeger.cyk.parser.grammar
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CYKTests {

    @Test
    fun verifyThatTheCYKAlgorithmDetectsWordOfLanguage() {
        assertTrue {
            cyk("she eats a fish with a fork") {
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
            }.getOrElse { error(it) }.result
        }
    }

    @Test
    fun verifyThatTheCYKAlgorithmDetectsWordIsNotPartOfLanguage() {
        assertFalse {
            cyk("she eats the fish with the fork") {
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
            }.getOrElse { error(it) }.result
        }
    }

    @Test
    fun verifyThatTheCYKAlgorithmDetectsEmptyWords() {
        assertTrue {
            cyk("") {
                grammar("S", includeEmptyProductionRule = true) { "" }
            }.getOrElse { error(it) }.result
        }
    }

    @Test
    fun verifyThatTheCYKAlgorithmDoesntDetectsEmptyWordsIncorrectlyExplicitly() {
        assertFalse {
            cyk("") {
                grammar("S", includeEmptyProductionRule = false) { "" }
            }.getOrElse { error(it) }.result
        }
    }

    @Test
    fun verifyThatTheCYKAlgorithmDoesntDetectsEmptyWordsIncorrectlyImplicitly() {
        assertFalse {
            cyk("") {
                grammar("S") { "" }
            }.getOrElse { error(it) }.result
        }
    }
}
