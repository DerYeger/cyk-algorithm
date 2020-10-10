package eu.yeger.cyk

import com.github.michaelbull.result.*
import kotlin.collections.fold

typealias Line = String

private val productionRuleRegex = Regex("[A-Z]+[a-z]* -> ([A-Z]+[a-z]* [A-Z]+[a-z]*|[a-z]+|${epsilon})")

fun grammar(startSymbol: String, block: () -> String): Result<Grammar, String> {
    return parseAsGrammar(rulesString = block(), startSymbol = startSymbol)
}

fun parseAsGrammar(rulesString: String, startSymbol: String): Result<Grammar, String> {
    return parse(rulesString, startSymbol)
        .map { productionRuleSet -> Grammar derivedFrom productionRuleSet }
}

fun parse(string: String, startSymbol: String): Result<ProductionRuleSet, String> {
    return string.trimIndent().lines().parseLines(startSymbol)
}

private fun List<Line>.parseLines(startSymbol: String): Result<ProductionRuleSet, String> {
    return fold(Ok(emptyList())) { productionRules: Result<List<ProductionRule>, String>, line: Line ->
        productionRules
            .and(line.parseLine(startSymbol))
            .andThen { productionRule: ProductionRule -> Ok(productionRules.getOr(emptyList()) + productionRule) }
    }.map { productionRules -> productionRuleSet(productionRules) }
}

private fun Line.parseLine(startSymbol: String): Result<ProductionRule, String> {
    return trim()
        .splitIntoComponents()
        .andThen { components -> components.asProductionRule(startSymbol) }
}

private fun Line.splitIntoComponents(): Result<List<String>, String> {
    return when {
        this matches productionRuleRegex -> Ok(split("->", " ").filter { it.isNotBlank() })
        else -> Err("Invalid production rule: $this")
    }
}

private fun List<String>.asProductionRule(startSymbol: String): Result<ProductionRule, String> {
    val inputSymbol = when (val inputString = get(0)) {
        startSymbol -> StartSymbol(inputString)
        else -> RegularNonTerminalSymbol(inputString)
    }
    return when (size) {
        3 -> asNonTerminatingProductionRule(inputSymbol)
        2 -> asTerminatingProductionRule(inputSymbol)
        else -> Err("Invalid component amount! Must be 2 or 3")
    }
}

private fun List<String>.asNonTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<NonTerminatingRule, String> {
    return Ok(NonTerminatingRule(inputSymbol, RegularNonTerminalSymbol(get(1)), RegularNonTerminalSymbol(get(2))))
}

private fun List<String>.asTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<TerminatingRule, String> {
    val outputSymbol = when (val terminalSymbol = get(1)) {
        epsilon -> RegularTerminalSymbol(terminalSymbol)
        else -> RegularTerminalSymbol(terminalSymbol)
    }
    return Ok(TerminatingRule(inputSymbol, outputSymbol))
}