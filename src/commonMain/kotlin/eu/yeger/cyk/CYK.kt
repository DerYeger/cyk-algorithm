package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    val cykModel = (0..inputString.count()).fold(CYKModel(inputString)) { cykModel: CYKModel, index: Int ->
        when (index) {
            0 -> cykModel.propagateTerminalProductionRules(grammar)
            else -> cykModel.propagateNonTerminalProductionRules(index + 1, grammar)
        }
    }

    println(cykModel)

    return cykModel.isTrue()
}

private fun CYKModel.propagateTerminalProductionRules(grammar: Grammar): CYKModel {
    return inputString.foldIndexed(this) { terminalSymbolIndex: Int, cykModel: CYKModel, terminalSymbol: TerminalSymbol ->
        cykModel.findProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex, grammar)
    }
}

private fun CYKModel.findProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
    grammar: Grammar,
): CYKModel {
    return grammar.productionRuleSet.terminatingRules.fold(this) { cykModel: CYKModel, terminatingRule: TerminatingRule ->
        when {
            terminatingRule produces terminalSymbol -> cykModel.add(0, terminalSymbolIndex, terminatingRule.input)
            else -> cykModel
        }
    }
}

private fun CYKModel.propagateNonTerminalProductionRules(l: Int, grammar: Grammar): CYKModel {
    return (1..(inputString.count() - l + 1)).fold(this) { lModel: CYKModel, s: Int ->
        (1 until l).fold(lModel) { pModel: CYKModel, p: Int ->
            pModel.findProductionRulesForNonTerminalSymbols(l = l, s = s, p = p, grammar = grammar)
        }
    }
}

private fun CYKModel.findProductionRulesForNonTerminalSymbols(
    l: Int,
    s: Int,
    p: Int,
    grammar: Grammar,
): CYKModel {
    return grammar.productionRuleSet.nonTerminatingRules.fold(this) { cykModel: CYKModel, nonTerminatingRule: NonTerminatingRule ->
        when {
            cykModel[p - 1, s - 1].contains(nonTerminatingRule.firstNonTerminatingSymbol)
                    && cykModel[l - p - 1, s + p - 1].contains(nonTerminatingRule.secondNonTerminatingSymbol) ->
                cykModel.add(
                    l - 1,
                    s - 1,
                    nonTerminatingRule.input
                )
            else -> cykModel
        }
    }
}
