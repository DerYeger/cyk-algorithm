package eu.yeger.cyk

import com.github.michaelbull.result.getOrElse
import org.junit.Test
import kotlin.test.assertTrue

class CYKTests {

    @Test
    fun `verify that the cyk algorithm detects word of language`() {
        assertTrue {
            val grammar = grammar("S") {
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
            }.getOrElse { error("Grammar was invalid") }
            val word = word { "she eats a fish with a fork" }
            cyk(grammar, word)
        }
    }
}
