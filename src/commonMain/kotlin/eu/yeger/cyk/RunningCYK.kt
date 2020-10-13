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
    return (0..word.size).fold(listOf(cykStartState)) { previousStates: List<CYKState>, l: Int ->
        when (l) {
            0 -> previousStates.runningPropagateTerminalProductionRules()
            else -> previousStates.runningPropagateNonTerminalProductionRules(l + 1)
        }
    }
}

private fun List<CYKState>.runningPropagateTerminalProductionRules(): List<CYKState> {
    return last().cykModel.word.foldIndexed(this) { terminalSymbolIndex: Int, previousStates: List<CYKState>, terminalSymbol: TerminalSymbol ->
        previousStates.runningFindProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex)
    }
}

private fun List<CYKState>.runningFindProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
): List<CYKState> {
    return last().cykModel.grammar.productionRuleSet.terminatingRules.fold(this) { previousStates: List<CYKState>, terminatingRule: TerminatingRule ->
        val lastState = previousStates.last()
        previousStates + when {
            terminatingRule produces terminalSymbol -> lastState.stepWithRuleAt(
                terminatingRule,
                rowIndex = 0,
                columnIndex = terminalSymbolIndex,
                listOf(Coordinates(-1, terminalSymbolIndex))
            )
            else -> lastState.stepWithoutRuleAt(
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
    return last().cykModel.grammar.productionRuleSet.nonTerminatingRules.fold(this) { previousStates: List<CYKState>, nonTerminatingRule: NonTerminatingRule ->
        val lastState = previousStates.last()
        previousStates + when {
            lastState.cykModel.allowsNonTerminalRuleAt(
                nonTerminatingRule,
                l = l,
                s = s,
                p = p
            ) -> lastState.stepWithRuleAt(
                nonTerminatingRule,
                rowIndex = l - 1,
                columnIndex = s - 1,
                listOf(Coordinates(p - 1, s - 1), Coordinates(l - p - 1, s + p - 1))
            )
            else -> lastState.stepWithoutRuleAt(
                nonTerminatingRule,
                rowIndex = l - 1,
                columnIndex = s - 1,
                listOf(Coordinates(p - 1, s - 1), Coordinates(l - p - 1, s + p - 1))
            )
        }
    }
}
