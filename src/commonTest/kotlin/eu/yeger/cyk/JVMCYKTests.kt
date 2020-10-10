package eu.yeger.cyk

import kotlin.test.Test

class JVMCYKTests {

    @Test
    fun `verify that the cyk algorithm detects word of language`() {
        val symbols = setOf("NP", "VP", "PP", "V", "P", "N", "Det")
            .asSymbolMap()
            .plus("S" to StartSymbol("S"))
        val rules = setOf(
            NonTerminatingRule(symbols["S"]!!, symbols["NP"]!!, symbols["VP"]!!),
            NonTerminatingRule(symbols["VP"]!!, symbols["VP"]!!, symbols["PP"]!!),
            NonTerminatingRule(symbols["VP"]!!, symbols["V"]!!, symbols["NP"]!!),
            TerminatingRule(symbols["VP"]!!, CustomTerminalSymbol("eats")),
            NonTerminatingRule(symbols["PP"]!!, symbols["P"]!!, symbols["NP"]!!),
            NonTerminatingRule(symbols["NP"]!!, symbols["Det"]!!, symbols["N"]!!),
            TerminatingRule(symbols["NP"]!!, CustomTerminalSymbol("she")),
            TerminatingRule(symbols["V"]!!, CustomTerminalSymbol("eats")),
            TerminatingRule(symbols["P"]!!, CustomTerminalSymbol("with")),
            TerminatingRule(symbols["N"]!!, CustomTerminalSymbol("fish")),
            TerminatingRule(symbols["N"]!!, CustomTerminalSymbol("fork")),
            TerminatingRule(symbols["Det"]!!, CustomTerminalSymbol("a")),
        )

        val grammar = Grammar(
            symbols = symbols.values.toSet(),
            startSymbol = symbols["S"]!! as StartSymbol,
            productionsRules = rules,
        )

        val inputString = sequenceOf(
            CustomTerminalSymbol("she"),
            CustomTerminalSymbol("eats"),
            CustomTerminalSymbol("a"),
            CustomTerminalSymbol("fish"),
            CustomTerminalSymbol("with"),
            CustomTerminalSymbol("a"),
            CustomTerminalSymbol("fork"),
        )

        println(cyk(grammar, inputString))
    }
}

private fun Set<String>.asSymbolMap(): Map<String, NonTerminalSymbol> {
    return associateWith { RegularNonTerminalSymbol(it) }
}