package eu.yeger.cyk.model

public data class ProductionRuleSet(
    val nonTerminatingRules: Set<NonTerminatingRule>,
    val terminatingRules: Set<TerminatingRule>
) : Set<ProductionRule> by (nonTerminatingRules + terminatingRules)

@Suppress("UNCHECKED_CAST")
public fun productionRuleSet(
    productionsRules: List<ProductionRule>,
): ProductionRuleSet {
    return productionsRules.groupBy { it::class }.let { groups ->
        ProductionRuleSet(
            nonTerminatingRules = (groups[NonTerminatingRule::class]?.toSet() ?: emptySet()) as Set<NonTerminatingRule>,
            terminatingRules = (groups[TerminatingRule::class]?.toSet() ?: emptySet()) as Set<TerminatingRule>,
        )
    }
}
