package eu.yeger.cyk

interface Symbol {
    val symbol: String
}

interface TerminalSymbol : Symbol

object EmptyString : TerminalSymbol {
    override val symbol: String = "ε"

    override fun toString() = symbol
}

data class RegularTerminalSymbol(override val symbol: String) : TerminalSymbol {
    override fun toString() = symbol
}

interface NonTerminalSymbol : Symbol

data class StartSymbol(override val symbol: String): NonTerminalSymbol {
    override fun toString() = symbol
}

data class RegularNonTerminalSymbol(override val symbol: String) : NonTerminalSymbol {
    override fun toString() = symbol
}
