package eu.yeger.cyk.model

sealed class ProductionRule(
    val left: NonTerminalSymbol,
    val right: Sequence<Symbol>,
) {
    override fun toString(): String {
        return "$left -> " + right.joinToString(" ")
    }
}

class NonTerminatingRule(
    input: NonTerminalSymbol,
    val firstRight: NonTerminalSymbol,
    val secondRight: NonTerminalSymbol,
) : ProductionRule(input, sequenceOf(firstRight, secondRight))

class TerminatingRule(
    input: NonTerminalSymbol,
    val terminalSymbol: TerminalSymbol,
) : ProductionRule(input, sequenceOf(terminalSymbol))

infix fun TerminatingRule.produces(terminalSymbol: TerminalSymbol): Boolean {
    return this.terminalSymbol == terminalSymbol
}
