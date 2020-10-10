package eu.yeger.cyk

class Grammar
private constructor(
    val symbols: Set<Symbol>,
    val startSymbol: StartSymbol,
    val productionsRules: ProductionRuleSet,
) {
    companion object {
        infix fun derivedFrom(productionsRules: ProductionRuleSet): Grammar? {
            val symbols = productionsRules.map(ProductionRule::input).toSet()

            val startSymbols = symbols.filterIsInstance<StartSymbol>()
            if (startSymbols.size != 1) return null // either no or too many start symbols

            return Grammar(symbols, startSymbols.first(), productionsRules)
        }
    }
}
