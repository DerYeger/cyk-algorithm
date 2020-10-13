package eu.yeger.cyk

import eu.yeger.cyk.model.*

public fun runningCYK(
    inputString: String,
    block: () -> Result<Grammar>,
): Result<List<CYKState>> {
    return block().map { grammar -> runningCYK(Word(inputString), grammar) }
}

public fun runningCYK(
    word: Word,
    grammar: Grammar,
): List<CYKState> {
    val cykStartState = CYKStart(CYKModel(word, grammar))
    return (0..word.size).fold(listOf(cykStartState)) { previousSteps: List<CYKState>, l: Int ->
        when (l) {
            0 -> previousSteps.runningPropagateTerminalProductionRules()
            else -> previousSteps.runningPropagateNonTerminalProductionRules(l + 1)
        }
    }
}

private fun List<CYKState>.runningPropagateTerminalProductionRules(): List<CYKState> {
    return last().cykModel.word.foldIndexed(this) { terminalSymbolIndex: Int, previousSteps: List<CYKState>, terminalSymbol: TerminalSymbol ->
        previousSteps.runningFindProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex)
    }
}

private fun List<CYKState>.runningFindProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
): List<CYKState> {
    return last().cykModel.grammar.productionRuleSet.terminatingRules.fold(this) { previousSteps: List<CYKState>, terminatingRule: TerminatingRule ->
        val lastStep = previousSteps.last()
        previousSteps + when {
            terminatingRule produces terminalSymbol -> lastStep.stepWithRuleAt(
                terminatingRule,
                rowIndex = 0,
                columnIndex = terminalSymbolIndex,
                listOf(Coordinates(-1, terminalSymbolIndex))
            )
            else -> lastStep.stepWithoutRuleAt(
                terminatingRule,
                rowIndex = 0,
                columnIndex = terminalSymbolIndex,
                listOf(Coordinates(-1, terminalSymbolIndex))
            )
        }
    }
}

private fun List<CYKState>.runningPropagateNonTerminalProductionRules(
    l: Int,
): List<CYKState> {
    return (1..(last().cykModel.word.size - l + 1)).fold(this) { rowSteps: List<CYKState>, s: Int ->
        (1 until l).fold(rowSteps) { columnSteps: List<CYKState>, p: Int ->
            columnSteps.runningFindProductionRulesForNonTerminalSymbols(l = l, s = s, p = p)
        }
    }
}

private fun List<CYKState>.runningFindProductionRulesForNonTerminalSymbols(
    l: Int,
    s: Int,
    p: Int,
): List<CYKState> {
    return last().cykModel.grammar.productionRuleSet.nonTerminatingRules.fold(this) { previousSteps: List<CYKState>, nonTerminatingRule: NonTerminatingRule ->
        val lastStep = previousSteps.last()
        previousSteps + when {
            lastStep.cykModel.allowsNonTerminalRuleAt(
                nonTerminatingRule,
                l = l,
                s = s,
                p = p
            ) -> lastStep.stepWithRuleAt(
                nonTerminatingRule,
                rowIndex = l - 1,
                columnIndex = s - 1,
                listOf(Coordinates(p - 1, s - 1), Coordinates(l - p - 1, s + p - 1))
            )
            else -> lastStep.stepWithoutRuleAt(
                nonTerminatingRule,
                rowIndex = l - 1,
                columnIndex = s - 1,
                listOf(Coordinates(p - 1, s - 1), Coordinates(l - p - 1, s + p - 1))
            )
        }
    }
}
