package eu.yeger.cyk.model

public sealed class ProductionRule {
    public abstract val left: NonTerminalSymbol
}

public data class NonTerminatingRule(
    public override val left: NonTerminalSymbol,
    public val right: Pair<NonTerminalSymbol, NonTerminalSymbol>
) : ProductionRule() {
    override fun toString(): String {
        return "$left -> ${right.first} ${right.second}"
    }
}

public data class TerminatingRule(
    public override val left: NonTerminalSymbol,
    public val right: TerminalSymbol,
) : ProductionRule() {
    override fun toString(): String {
        return "$left -> $right"
    }
}

public infix fun TerminatingRule.produces(terminalSymbol: TerminalSymbol): Boolean {
    return this.right == terminalSymbol && (terminalSymbol.symbol.isNotEmpty() || left is StartSymbol)
}
