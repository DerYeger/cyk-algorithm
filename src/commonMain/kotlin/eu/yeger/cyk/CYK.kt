package eu.yeger.cyk

import eu.yeger.cyk.model.*
import eu.yeger.cyk.model.withSymbolAt
import eu.yeger.cyk.parser.word

public fun cyk(
    wordString: String,
    grammar: () -> Result<Grammar>
): Result<CYKModel> {
    return word(wordString).with(grammar(), ::cyk)
}

public fun cyk(
    word: Result<Word>,
    grammar: Result<Grammar>
): Result<CYKModel> {
    return word.with(grammar, ::cyk)
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
            terminatingRule produces terminalSymbol -> cykModel.withSymbolAt(
                terminatingRule.left,
                rowIndex = 0,
                columnIndex = terminalSymbolIndex
            )
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
            cykModel.allowsNonTerminalRuleAt(nonTerminatingRule, l = l, s = s, p = p) -> cykModel.withSymbolAt(
                nonTerminatingRule.left,
                rowIndex = l - 1,
                columnIndex = s - 1
            )
            else -> cykModel
        }
    }
}
