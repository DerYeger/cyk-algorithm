package eu.yeger.cyk.model

public interface Symbol {
    public val symbol: String
}

public data class TerminalSymbol(override val symbol: String) : Symbol {
    override fun toString(): String = symbol
}

public interface NonTerminalSymbol : Symbol

public data class StartSymbol(override val symbol: String) : NonTerminalSymbol {
    override fun toString(): String = symbol
}

public data class RegularNonTerminalSymbol(override val symbol: String) : NonTerminalSymbol {
    override fun toString(): String = symbol
}
