package eu.yeger.cyk

data class ProductionRuleSet(
    val nonTerminatingRules: Set<NonTerminatingRule>,
    val terminatingRules: Set<TerminatingRule>
) : Set<ProductionRule> by (nonTerminatingRules + terminatingRules)

@Suppress("UNCHECKED_CAST")
fun productionRuleSet(
    productionsRules: List<ProductionRule>,
): ProductionRuleSet {
    return productionsRules.groupBy { it::class }.let { groups ->
        ProductionRuleSet(
            nonTerminatingRules = groups[NonTerminatingRule::class]?.toSet() as Set<NonTerminatingRule>,
            terminatingRules = groups[TerminatingRule::class]?.toSet() as Set<TerminatingRule>,
        )
    }
}

fun productionRuleSet(
    nonTerminatingRules: Set<NonTerminatingRule>,
    terminatingRules: Set<TerminatingRule>,
): ProductionRuleSet {
    return ProductionRuleSet(
        nonTerminatingRules = nonTerminatingRules,
        terminatingRules = terminatingRules,
    )
}

fun nonTerminatingRules(vararg nonTerminatingRules: NonTerminatingRule): Set<NonTerminatingRule> {
    return setOf(*nonTerminatingRules)
}

fun terminatingRules(vararg terminatingRules: TerminatingRule): Set<TerminatingRule> {
    return setOf(*terminatingRules)
}
