package eu.yeger.cyk.model

public class Grammar(
    public val startSymbol: StartSymbol,
    public val includesEmptyProductionRule: Boolean,
    public val productionRuleSet: ProductionRuleSet,
)
