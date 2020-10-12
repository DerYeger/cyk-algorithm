package eu.yeger.cyk

import eu.yeger.cyk.model.*
import eu.yeger.cyk.model.emptyProductionRuleCYKModel
import eu.yeger.cyk.model.withSymbolAt

public fun runningCYK(
    inputString: String,
    block: () -> Result<Grammar>,
): Result<List<CYKModel>> {
    return block().map { grammar -> runningCYK(word(inputString), grammar) }
}

public fun runningCYK(
    word: Word,
    grammar: Grammar,
): List<CYKModel> {
    if (word.count() == 1 && word.first().symbol.isEmpty() && grammar.includesEmptyProductionRule) {
        return listOf(emptyProductionRuleCYKModel(word, grammar))
    }
    return (0..word.count()).fold(listOf(CYKModel(word, grammar))) { previousModels: List<CYKModel>, l: Int ->
        when (l) {
            0 -> previousModels.runningPropagateTerminalProductionRules()
            else -> previousModels.runningPropagateNonTerminalProductionRules(l + 1)
        }
    }
}

private fun List<CYKModel>.runningPropagateTerminalProductionRules(): List<CYKModel> {
    return last().word.foldIndexed(this) { terminalSymbolIndex: Int, previousModels: List<CYKModel>, terminalSymbol: TerminalSymbol ->
        previousModels.runningFindProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex)
    }
}

private fun List<CYKModel>.runningFindProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
): List<CYKModel> {
    return last().grammar.productionRuleSet.terminatingRules.fold(this) { previousModels: List<CYKModel>, terminatingRule: TerminatingRule ->
        val lastModel = previousModels.last()
        previousModels + when {
            terminatingRule produces terminalSymbol -> lastModel.withSymbolAt(terminatingRule.left, 0, terminalSymbolIndex)
            else -> lastModel
        }
    }
}

private fun List<CYKModel>.runningPropagateNonTerminalProductionRules(
    l: Int,
): List<CYKModel> {
    return (1..(last().word.count() - l + 1)).fold(this) { rowModels: List<CYKModel>, s: Int ->
        (1 until l).fold(rowModels) { columnModels: List<CYKModel>, p: Int ->
            columnModels.runningFindProductionRulesForNonTerminalSymbols(l = l, s = s, p = p)
        }
    }
}

private fun List<CYKModel>.runningFindProductionRulesForNonTerminalSymbols(
    l: Int,
    s: Int,
    p: Int,
): List<CYKModel> {
    return last().grammar.productionRuleSet.nonTerminatingRules.fold(this) { previousModels: List<CYKModel>, rule: NonTerminatingRule ->
        val lastModel = previousModels.last()
        previousModels + when {
            lastModel.allowsNonTerminalRuleAt(rule, l = l, s = s, p = p) -> lastModel.withSymbolAt(rule.left, l - 1, s - 1)
            else -> lastModel
        }
    }
}
