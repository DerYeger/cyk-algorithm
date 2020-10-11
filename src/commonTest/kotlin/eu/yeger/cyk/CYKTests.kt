package eu.yeger.cyk

import kotlin.test.Test
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
            }.getOrElse { error(it) }
        }
    }
}
