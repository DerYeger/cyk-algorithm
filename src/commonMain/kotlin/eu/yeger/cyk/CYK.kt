package eu.yeger.cyk

import eu.yeger.cyk.model.*
import eu.yeger.cyk.model.withSymbolAt

public fun cyk(
    inputString: String,
    block: () -> Result<Grammar>,
): Result<CYKModel> {
    return block().map { grammar -> cyk(Word(inputString), grammar) }
}

public fun cyk(
    word: Word,
    grammar: Grammar,
): CYKModel {
    return (0..word.size).fold(CYKModel(word, grammar)) { cykModel: CYKModel, l: Int ->
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
    return (1..(word.size - l + 1)).fold(this) { rowModel: CYKModel, s: Int ->
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
    return grammar.productionRuleSet.nonTerminatingRules.fold(this) { cykModel: CYKModel, nonTerminatingRule: NonTerminatingRule ->
        when {
            cykModel.allowsNonTerminalRuleAt(nonTerminatingRule, l = l, s = s, p = p) -> cykModel.withSymbolAt(nonTerminatingRule.left, l - 1, s - 1)
            else -> cykModel
        }
    }
}
