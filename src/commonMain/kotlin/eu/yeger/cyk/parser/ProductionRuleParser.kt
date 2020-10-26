package eu.yeger.cyk.parser

import eu.yeger.cyk.*
import eu.yeger.cyk.model.*

public fun parseProductionRules(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    productionRules: String,
): Result<ProductionRuleSet> {
    return startSymbol
        .parseStartSymbol()
        .andThen { validatedStartSymbol ->
            productionRules
                .trimIndent()
                .lines()
                .filter(String::isNotBlank)
                .parseLines(validatedStartSymbol)
        }
        .map { productionRuleSet ->
            when (includeEmptyProductionRule) {
                true -> productionRuleSet.copy(
                    terminatingRules = productionRuleSet.terminatingRules + TerminatingRule(
                        StartSymbol(startSymbol),
                        TerminalSymbol("")
                    )
                )
                false -> productionRuleSet
            }
        }
}

private fun List<String>.parseLines(startSymbol: StartSymbol): Result<ProductionRuleSet> {
    return mapAsResult { parseLine(startSymbol) }
        .map(::productionRuleSet)
}

private fun String.parseLine(startSymbol: StartSymbol): Result<ProductionRule> {
    return trim()
        .splitIntoComponents()
        .andThen { components -> components.asProductionRule(startSymbol) }
}

private fun String.splitIntoComponents(): Result<List<String>> {
    return when {
        this matches productionRuleRegex -> split("->", " ").filter(String::isNotBlank).asSuccess()
        else -> fail("Invalid production rule. ($this)")
    }
}

private fun List<String>.asProductionRule(startSymbol: StartSymbol): Result<ProductionRule> {
    return when (val inputString = get(0)) {
        startSymbol.symbol -> startSymbol.asSuccess()
        else -> inputString.parseNonTerminalSymbol()
    }.andThen { inputSymbol ->
        when (size) {
            3 -> asNonTerminatingProductionRule(inputSymbol)
            2 -> asTerminatingProductionRule(inputSymbol)
            else -> fail("Invalid amount of symbols on the right side! Must be 1 for terminal or 2 for non terminal production rules. ($lastIndex)")
        }
    }
}

private fun List<String>.asNonTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<NonTerminatingRule> {
    return get(1)
        .parseNonTerminalSymbol()
        .with(get(2).parseNonTerminalSymbol()) { firstRight, secondRight ->
            NonTerminatingRule(
                left = inputSymbol,
                right = firstRight to secondRight
            )
        }
}

private fun List<String>.asTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<TerminatingRule> {
    return get(1)
        .parseTerminalSymbol()
        .map { terminalSymbol -> TerminatingRule(inputSymbol, terminalSymbol) }
}
