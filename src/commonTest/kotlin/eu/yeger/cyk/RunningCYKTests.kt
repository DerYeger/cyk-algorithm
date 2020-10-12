package eu.yeger.cyk

import eu.yeger.cyk.model.result
import eu.yeger.cyk.parser.grammar
import kotlin.test.Test
import kotlin.test.assertTrue

class RunningCYKTests {

    @Test
    fun verifyThatTheRunningCYKAlgorithmDetectsWordOfLanguage() {
        val models = runningCYK("she eats a fish with a fork") {
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
        models.forEach {
            println(it)
            println()
        }
        assertTrue(models.last().result)
    }
}
