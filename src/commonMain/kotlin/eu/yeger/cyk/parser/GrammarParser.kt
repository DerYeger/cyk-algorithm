package eu.yeger.cyk.parser

import eu.yeger.cyk.Result
import eu.yeger.cyk.map
import eu.yeger.cyk.model.Grammar
import eu.yeger.cyk.model.StartSymbol

public fun grammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    block: () -> String,
): Result<Grammar> {
    return grammar(
        startSymbol = startSymbol,
        includeEmptyProductionRule = includeEmptyProductionRule,
        productionRules = block(),
    )
}

public fun grammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    productionRules: String,
): Result<Grammar> {
    return parseProductionRules(
        startSymbol = startSymbol,
        includeEmptyProductionRule = includeEmptyProductionRule,
        productionRules = productionRules
    ).map { productionRuleSet ->
        Grammar(
            startSymbol = StartSymbol(startSymbol),
            productionRuleSet = productionRuleSet,
        )
    }
}
