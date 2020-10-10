package eu.yeger.cyk

import kotlin.test.Test
import kotlin.test.assertTrue

class JVMCYKTests {

    @Test
    fun `verify that the cyk algorithm detects word of language`() {
        val symbols = setOf("NP", "VP", "PP", "V", "P", "N", "Det")
            .asSymbolMap()
            .plus("S" to StartSymbol("S"))

        val productionRuleSet = productionRuleSet(
            nonTerminatingRules = nonTerminatingRules(
                NonTerminatingRule(symbols["S"]!!, symbols["NP"]!!, symbols["VP"]!!),
                NonTerminatingRule(symbols["VP"]!!, symbols["VP"]!!, symbols["PP"]!!),
                NonTerminatingRule(symbols["VP"]!!, symbols["V"]!!, symbols["NP"]!!),
                NonTerminatingRule(symbols["PP"]!!, symbols["P"]!!, symbols["NP"]!!),
                NonTerminatingRule(symbols["NP"]!!, symbols["Det"]!!, symbols["N"]!!),
            ),
            terminatingRules = terminatingRules(
                TerminatingRule(symbols["VP"]!!, RegularTerminalSymbol("eats")),
                TerminatingRule(symbols["NP"]!!, RegularTerminalSymbol("she")),
                TerminatingRule(symbols["V"]!!, RegularTerminalSymbol("eats")),
                TerminatingRule(symbols["P"]!!, RegularTerminalSymbol("with")),
                TerminatingRule(symbols["N"]!!, RegularTerminalSymbol("fish")),
                TerminatingRule(symbols["N"]!!, RegularTerminalSymbol("fork")),
                TerminatingRule(symbols["Det"]!!, RegularTerminalSymbol("a")),
            ),
        )

        val grammar = requireNotNull(Grammar derivedFrom productionRuleSet)

        val inputString = sequenceOf(
            RegularTerminalSymbol("she"),
            RegularTerminalSymbol("eats"),
            RegularTerminalSymbol("a"),
            RegularTerminalSymbol("fish"),
            RegularTerminalSymbol("with"),
            RegularTerminalSymbol("a"),
            RegularTerminalSymbol("fork"),
        )

        assertTrue(cyk(grammar, inputString))
    }
}

private fun Set<String>.asSymbolMap(): Map<String, NonTerminalSymbol> {
    return associateWith { RegularNonTerminalSymbol(it) }
}