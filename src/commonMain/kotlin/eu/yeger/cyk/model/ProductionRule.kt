package eu.yeger.cyk.model

public sealed class ProductionRule(
    public val left: NonTerminalSymbol,
    private val right: List<Symbol>,
) {
    override fun toString(): String {
        return "$left -> " + right.joinToString(" ")
    }
}

public class NonTerminatingRule(
    input: NonTerminalSymbol,
    public val firstRight: NonTerminalSymbol,
    public val secondRight: NonTerminalSymbol,
) : ProductionRule(input, listOf(firstRight, secondRight))

public class TerminatingRule(
    input: NonTerminalSymbol,
    public val terminalSymbol: TerminalSymbol,
) : ProductionRule(input, listOf(terminalSymbol))

public infix fun TerminatingRule.produces(terminalSymbol: TerminalSymbol): Boolean {
    return this.terminalSymbol == terminalSymbol && (terminalSymbol.symbol.isNotEmpty() || left is StartSymbol)
}
