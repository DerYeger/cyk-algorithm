package eu.yeger.cyk

data class Grammar(
    val symbols: Set<Symbol>,
    val startSymbol: StartSymbol,
    val productionsRules: Set<ProductionRule>,
)

fun Grammar.validatedOrNull(): Grammar? {
    if (symbols.contains(startSymbol).not()) {
        // invalid start symbol
        return null
    }

    if (productionsRules.any { productionRule -> symbols.contains(productionRule.input).not() }) {
        // missing symbol
        return null
    }

    return this
}
