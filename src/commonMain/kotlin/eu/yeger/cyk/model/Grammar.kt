package eu.yeger.cyk.model

class Grammar
private constructor(
    val symbols: Set<Symbol>,
    val startSymbol: StartSymbol,
    val productionRuleSet: ProductionRuleSet,
) {
    companion object {
        infix fun derivedFrom(productionsRules: ProductionRuleSet): Grammar {
            val symbols = productionsRules.map(ProductionRule::left).toSet()
            return Grammar(symbols, symbols.first { it is StartSymbol } as StartSymbol, productionsRules)
        }
    }
}
