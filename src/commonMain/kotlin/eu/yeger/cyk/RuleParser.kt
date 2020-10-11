package eu.yeger.cyk

import kotlin.collections.fold

typealias Line = String

private val productionRuleRegex = Regex("[A-Z]+[a-z]* -> ([A-Z]+[a-z]* [A-Z]+[a-z]*|[a-z]+|$epsilon)")

fun grammar(startSymbol: String, block: () -> String): Result<Grammar> {
    return parseAsGrammar(rulesString = block(), startSymbol = startSymbol)
}

fun parseAsGrammar(rulesString: String, startSymbol: String): Result<Grammar> {
    return parse(rulesString, startSymbol)
        .map { productionRuleSet -> Grammar derivedFrom productionRuleSet }
}

fun parse(string: String, startSymbol: String): Result<ProductionRuleSet> {
    return string.trimIndent().lines().parseLines(startSymbol)
}

private fun List<Line>.parseLines(startSymbol: String): Result<ProductionRuleSet> {
    return fold(succeed(emptyList())) { productionRules: Result<List<ProductionRule>>, line: Line ->
        productionRules
            .and(line.parseLine(startSymbol))
            .andThen { productionRule: ProductionRule -> succeed(productionRules.getOr(emptyList()) + productionRule) }
    }.map { productionRules -> productionRuleSet(productionRules) }
}

private fun Line.parseLine(startSymbol: String): Result<ProductionRule> {
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

private fun List<String>.asProductionRule(startSymbol: String): Result<ProductionRule> {
    val inputSymbol = when (val inputString = get(0)) {
        startSymbol -> StartSymbol(inputString)
        else -> RegularNonTerminalSymbol(inputString)
    }
    return when (size) {
        3 -> asNonTerminatingProductionRule(inputSymbol)
        2 -> asTerminatingProductionRule(inputSymbol)
        else -> fail("Invalid component amount! Must be 2 or 3")
    }
}

private fun List<String>.asNonTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<NonTerminatingRule> {
    return succeed(NonTerminatingRule(inputSymbol, RegularNonTerminalSymbol(get(1)), RegularNonTerminalSymbol(get(2))))
}

private fun List<String>.asTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<TerminatingRule> {
    val outputSymbol = when (val terminalSymbol = get(1)) {
        epsilon -> RegularTerminalSymbol(terminalSymbol)
        else -> RegularTerminalSymbol(terminalSymbol)
    }
    return succeed(TerminatingRule(inputSymbol, outputSymbol))
}
