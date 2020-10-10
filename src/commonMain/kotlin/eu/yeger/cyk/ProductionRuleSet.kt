package eu.yeger.cyk

data class ProductionRuleSet(
    val nonTerminatingRules: Set<NonTerminatingRule>,
    val terminatingRules: Set<TerminatingRule>,
    val emptyProductionRule: EmptyProductionRule?
): Set<ProductionRule> by (nonTerminatingRules + terminatingRules + setOfNotNull(emptyProductionRule))

fun productionRuleSet(
    nonTerminatingRules: Set<NonTerminatingRule>,
    terminatingRules: Set<TerminatingRule>,
    emptyProductionRule: EmptyProductionRule? =  null
): ProductionRuleSet {
    return ProductionRuleSet(
        nonTerminatingRules = nonTerminatingRules,
        terminatingRules = terminatingRules,
        emptyProductionRule = emptyProductionRule,
    )
}

fun nonTerminatingRules(vararg nonTerminatingRules: NonTerminatingRule): Set<NonTerminatingRule> {
    return setOf(*nonTerminatingRules)
}

fun terminatingRules(vararg terminatingRules: TerminatingRule): Set<TerminatingRule> {
    return setOf(*terminatingRules)
}

fun emptyProductionRuleFor(startSymbol: StartSymbol): EmptyProductionRule {
    return EmptyProductionRule(startSymbol)
}
