package eu.yeger.cyk.parser

import eu.yeger.cyk.*
import eu.yeger.cyk.model.*

private const val terminalSymbolRegexString = "[a-z]+"
private const val nonTerminalSymbolRegexString = "[A-Z]+[a-z]*"

public val terminalSymbolRegex: Regex = terminalSymbolRegexString.toRegex()
public val nonTerminalSymbolRegex: Regex = nonTerminalSymbolRegexString.toRegex()
public val productionRuleRegex: Regex = "$nonTerminalSymbolRegexString -> ($nonTerminalSymbolRegexString $nonTerminalSymbolRegexString|$terminalSymbolRegexString)".toRegex()

public fun grammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    block: () -> String,
): Result<Grammar> {
    return parseAsGrammar(
        startSymbol = startSymbol,
        includeEmptyProductionRule = includeEmptyProductionRule,
        productionRules = block(),
    )
}

public fun parseAsGrammar(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    productionRules: String,
): Result<Grammar> {
    return parse(
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

public fun parse(
    startSymbol: String,
    includeEmptyProductionRule: Boolean = false,
    productionRules: String,
): Result<ProductionRuleSet> {
    return validateStartSymbol(startSymbol)
        .andThen { validatedStartSymbol ->
            productionRules.trimIndent()
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
        startSymbol matches nonTerminalSymbolRegex -> succeed(StartSymbol(startSymbol))
        startSymbol.isBlank() -> fail("Start symbol cannot be blank.")
        else -> fail("Invalid start symbol: $startSymbol")
    }
}

private fun List<String>.parseLines(startSymbol: StartSymbol): Result<ProductionRuleSet> {
    return fold(succeed(emptyList())) { productionRules: Result<List<ProductionRule>>, line: String ->
        productionRules
            .and(line.parseLine(startSymbol))
            .andThen { productionRule: ProductionRule -> succeed(productionRules.getOr(emptyList()) + productionRule) }
    }.map { productionRules -> productionRuleSet(productionRules) }
}

private fun String.parseLine(startSymbol: StartSymbol): Result<ProductionRule> {
    return trim()
        .splitIntoComponents()
        .andThen { components -> components.asProductionRule(startSymbol) }
}

private fun String.splitIntoComponents(): Result<List<String>> {
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
        else -> fail("Invalid component amount ($size)! Must be 2 for terminal or 3 for non terminal production rules.")
    }
}

private fun List<String>.asNonTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<NonTerminatingRule> {
    return succeed(NonTerminatingRule(inputSymbol, RegularNonTerminalSymbol(get(1)) to RegularNonTerminalSymbol(get(2))))
}

private fun List<String>.asTerminatingProductionRule(inputSymbol: NonTerminalSymbol): Result<TerminatingRule> {
    val outputSymbol = when (val terminalSymbol = get(1)) {
        epsilon -> TerminalSymbol(terminalSymbol)
        else -> TerminalSymbol(terminalSymbol)
    }
    return succeed(TerminatingRule(inputSymbol, outputSymbol))
}
