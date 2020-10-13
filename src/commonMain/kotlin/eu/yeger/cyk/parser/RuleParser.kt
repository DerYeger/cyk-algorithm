package eu.yeger.cyk.parser

import eu.yeger.cyk.*
import eu.yeger.cyk.model.*

private val startSymbolRegex = Regex("[A-Z]+[a-z]*")

private val productionRuleRegex = Regex("[A-Z]+[a-z]* -> ([A-Z]+[a-z]* [A-Z]+[a-z]*|[a-z]+)")

public fun grammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    block: () -> String,
): Result<Grammar> {
    return parseAsGrammar(
        startSymbol = startSymbol,
        includeEmptyProductionRule = includeEmptyProductionRule,
        rulesString = block(),
    )
}

public fun parseAsGrammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    rulesString: String,
): Result<Grammar> {
    return parse(
        startSymbol = startSymbol,
        includeEmptyProductionRule = includeEmptyProductionRule,
        rulesString = rulesString
    ).map { productionRuleSet ->
        Grammar(
            startSymbol = StartSymbol(startSymbol),

            productionRuleSet = productionRuleSet,
        )
    }
}

public fun parse(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    rulesString: String,
): Result<ProductionRuleSet> {
    return validateStartSymbol(startSymbol)
        .andThen { validatedStartSymbol ->
            rulesString.trimIndent()
                .lines()
                .filter { it.isNotBlank() }
                .parseLines(validatedStartSymbol)
        }
        .map { productionRuleSet ->
            when (includeEmptyProductionRule) {
                true -> productionRuleSet.copy(terminatingRules = productionRuleSet.terminatingRules + TerminatingRule(StartSymbol(startSymbol), TerminalSymbol("")))
                else -> productionRuleSet
            }
        }
}

private fun validateStartSymbol(startSymbol: String): Result<StartSymbol> {
    return when {
        startSymbol matches startSymbolRegex -> succeed(StartSymbol(startSymbol))
        else -> fail("Invalid start symbol: $startSymbol")
    }
}

private fun List<Line>.parseLines(startSymbol: StartSymbol): Result<ProductionRuleSet> {
    return fold(succeed(emptyList())) { productionRules: Result<List<ProductionRule>>, line: Line ->
        productionRules
            .and(line.parseLine(startSymbol))
            .andThen { productionRule: ProductionRule -> succeed(productionRules.getOr(emptyList()) + productionRule) }
    }.map { productionRules -> productionRuleSet(productionRules) }
}

private fun Line.parseLine(startSymbol: StartSymbol): Result<ProductionRule> {
    return trim()
        .splitIntoComponents()
        .andThen { components -> components.asProductionRule(startSymbol) }
}

private fun Line.splitIntoComponents(): Result<List<String>> {
    return when {
        this matches productionRuleRegex -> succeed(split("->", " ").filter { it.isNotBlank() })
        else -> fail("Invalid production rule: $this")
    }
}

private fun List<String>.asProductionRule(startSymbol: StartSymbol): Result<ProductionRule> {
    val inputSymbol = when (val inputString = get(0)) {
        startSymbol.symbol -> startSymbol
        else -> RegularNonTerminalSymbol(inputString)
    }
    return when (size) {
        3 -> asNonTerminatingProductionRule(inputSymbol)
        2 -> asTerminatingProductionRule(inputSymbol)
        else -> fail("Invalid component amount ($size)! Must be 2 or 3")
    }
}

private fun List<String>.asNonTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<NonTerminatingRule> {
    return succeed(NonTerminatingRule(inputSymbol, RegularNonTerminalSymbol(get(1)), RegularNonTerminalSymbol(get(2))))
}

private fun List<String>.asTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<TerminatingRule> {
    val outputSymbol = when (val terminalSymbol = get(1)) {
        epsilon -> TerminalSymbol(terminalSymbol)
        else -> TerminalSymbol(terminalSymbol)
    }
    return succeed(TerminatingRule(inputSymbol, outputSymbol))
}
