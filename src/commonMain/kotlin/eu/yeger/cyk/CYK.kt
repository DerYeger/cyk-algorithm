package eu.yeger.cyk

import eu.yeger.cyk.model.*
import eu.yeger.cyk.model.withSymbolAt

public fun cyk(
    inputString: String,
    block: () -> Result<Grammar>,
): Result<CYKModel> {
    return block().map { grammar -> cyk(word(inputString), grammar) }
}

public fun cyk(
    word: Word,
    grammar: Grammar,
): CYKModel {
    if (word.count() == 1 && word.first().symbol.isEmpty() && grammar.includesEmptyProductionRule) {
        return emptyProductionRuleCYKModel(word, grammar)
    }
    return (0..word.count()).fold(CYKModel(word, grammar)) { cykModel: CYKModel, l: Int ->
        when (l) {
            0 -> cykModel.propagateTerminalProductionRules()
            else -> cykModel.propagateNonTerminalProductionRules(l + 1)
        }
    }
}

private fun CYKModel.propagateTerminalProductionRules(): CYKModel {
    return word.foldIndexed(this) { terminalSymbolIndex: Int, cykModel: CYKModel, terminalSymbol: TerminalSymbol ->
        cykModel.findProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex)
    }
}

private fun CYKModel.findProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
): CYKModel {
    return grammar.productionRuleSet.terminatingRules.fold(this) { cykModel: CYKModel, terminatingRule: TerminatingRule ->
        when {
            terminatingRule produces terminalSymbol -> cykModel.withSymbolAt(terminatingRule.left, 0, terminalSymbolIndex)
            else -> cykModel
        }
    }
}

private fun CYKModel.propagateNonTerminalProductionRules(
    l: Int,
): CYKModel {
    return (1..(word.count() - l + 1)).fold(this) { rowModel: CYKModel, s: Int ->
        (1 until l).fold(rowModel) { columnModel: CYKModel, p: Int ->
            columnModel.findProductionRulesForNonTerminalSymbols(l = l, s = s, p = p)
        }
    }
}

private fun CYKModel.findProductionRulesForNonTerminalSymbols(
    l: Int,
    s: Int,
    p: Int,
): CYKModel {
    return grammar.productionRuleSet.nonTerminatingRules.fold(this) { model: CYKModel, rule: NonTerminatingRule ->
        when {
            model.allowsNonTerminalRuleAt(rule, l = l, s = s, p = p) -> model.withSymbolAt(rule.left, l - 1, s - 1)
            else -> model
        }
    }
}

private fun CYKModel.allowsNonTerminalRuleAt(
    nonTerminatingRule: NonTerminatingRule,
    l: Int,
    s: Int,
    p: Int,
): Boolean {
    return this.containsSymbolAt(nonTerminatingRule.firstRight, p - 1, s - 1) &&
        this.containsSymbolAt(nonTerminatingRule.secondRight, l - p - 1, s + p - 1)
}
