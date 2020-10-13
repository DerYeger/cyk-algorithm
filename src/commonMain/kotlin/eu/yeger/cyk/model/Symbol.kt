package eu.yeger.cyk.model

import eu.yeger.cyk.epsilon

public sealed class Symbol {
    public abstract val symbol: String
}

public data class TerminalSymbol(public override val symbol: String) : Symbol() {
    override fun toString(): String = symbol.ifBlank { epsilon }
}

public sealed class NonTerminalSymbol : Symbol()

public data class StartSymbol(public override val symbol: String) : NonTerminalSymbol() {
    override fun toString(): String = symbol
}

public data class RegularNonTerminalSymbol(public override val symbol: String) : NonTerminalSymbol() {
    override fun toString(): String = symbol
}
