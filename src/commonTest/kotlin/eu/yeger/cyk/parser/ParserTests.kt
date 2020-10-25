package eu.yeger.cyk.parser

import eu.yeger.cyk.Result
import eu.yeger.cyk.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParserTests {

    @Test
    fun verifyThatParsingAGrammarWorks() {
        val result = grammar("S") {
            """
                S -> A B
                A -> hello
                B -> world
            """.trimIndent()
        } as Result.Success
        val grammar = result.data
        val expectedProductionRuleSet = productionRuleSet(
            listOf(
                NonTerminatingRule(StartSymbol("S"), RegularNonTerminalSymbol("A") to RegularNonTerminalSymbol("B")),
                TerminatingRule(RegularNonTerminalSymbol("A"), TerminalSymbol("hello")),
                TerminatingRule(RegularNonTerminalSymbol("B"), TerminalSymbol("world")),
            )
        )
        assertEquals(expectedProductionRuleSet, grammar.productionRuleSet)
        assertEquals(StartSymbol("S"), grammar.startSymbol)

        val secondResult = grammar("S") {
            """
                Start -> First Second
                Other -> test
            """.trimIndent()
        } as Result.Success
        val secondGrammar = secondResult.data
        val secondExpectedProductionRuleSet = productionRuleSet(
            listOf(
                NonTerminatingRule(RegularNonTerminalSymbol("Start"), RegularNonTerminalSymbol("First") to RegularNonTerminalSymbol("Second")),
                TerminatingRule(RegularNonTerminalSymbol("Other"), TerminalSymbol("test")),
            )
        )
        assertEquals(secondExpectedProductionRuleSet, secondGrammar.productionRuleSet)
        assertEquals(StartSymbol("S"), secondGrammar.startSymbol)
    }

    @Test
    fun verifyThatParsingAnInvalidGrammarDoesNotWork() {
        listOf(
            "S",
            "S -> A",
            "S -> a b",
            "S -> a b c",
            "S -> A b",
            "S -> a B",
        ).forEach { productionRule ->
            assertTrue {
                grammar("S") {
                    productionRule
                } is Result.Failure
            }
        }
    }
}
