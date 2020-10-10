package eu.yeger.cyk

sealed class ProductionRule(
    val input: NonTerminalSymbol,
    val output: Sequence<Symbol>,
)

class NonTerminatingRule(
    input: NonTerminalSymbol,
    val firstNonTerminatingSymbol: NonTerminalSymbol,
    val secondNonTerminatingSymbol: NonTerminalSymbol,
) : ProductionRule(input, sequenceOf(firstNonTerminatingSymbol, secondNonTerminatingSymbol)) {
    override fun toString(): String {
        return "$input -> $firstNonTerminatingSymbol $secondNonTerminatingSymbol"
    }
}

class TerminatingRule(
    input: NonTerminalSymbol,
    val terminalSymbol: TerminalSymbol,
) : ProductionRule(input, sequenceOf(terminalSymbol)) {
    override fun toString(): String {
        return "$input -> $terminalSymbol"
    }
}

infix fun TerminatingRule.produces(terminalSymbol: TerminalSymbol): Boolean {
    return this.terminalSymbol == terminalSymbol
}

class EmptyProductionRule(
    input: StartSymbol,
) : ProductionRule(input, sequenceOf(EmptySymbol)) {
    override fun toString(): String {
        return "$input -> $EmptySymbol"
    }
}
