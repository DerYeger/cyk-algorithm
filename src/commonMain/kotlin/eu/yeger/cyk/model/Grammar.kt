package eu.yeger.cyk.model

class Grammar(
    val startSymbol: StartSymbol,
    val includesEmptyProductionRule: Boolean,
    val productionRuleSet: ProductionRuleSet,
) {
    val symbols: Set<Symbol> = productionRuleSet.map(ProductionRule::left).toSet()
}
