package eu.yeger.cyk

import com.github.michaelbull.result.get
import org.junit.Test

class CYKTests {

    @Test
    fun `verify that the cyk algorithm detects word of language`() {
        val rules = """
            S->NP VP
            VP->VP PP
            VP->V NP
            VP->eats
            PP->P NP
            NP->Det N
            NP->she
            V->eats
            P->with
            N->fish
            N->fork
            Det->a
        """.trimIndent()
        val grammar = parseAsGrammar(rules, "S")

        val inputString = sequenceOf(
            RegularTerminalSymbol("she"),
            RegularTerminalSymbol("eats"),
            RegularTerminalSymbol("a"),
            RegularTerminalSymbol("fish"),
            RegularTerminalSymbol("with"),
            RegularTerminalSymbol("a"),
            RegularTerminalSymbol("fork"),
        )

        println(cyk(grammar.get()!!, inputString))
    }
}
