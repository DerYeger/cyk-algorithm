package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    val cykModel = (0..inputString.count()).fold(CYKModel(grammar, inputString)) { cykModel: CYKModel, l: Int ->
        when (l) {
            0 -> cykModel.propagateTerminalProductionRules()
            else -> cykModel.propagateNonTerminalProductionRules(l + 1)
        }
    }

    println(cykModel)

    return cykModel.result
}

private fun CYKModel.propagateTerminalProductionRules(): CYKModel {
    return inputString.foldIndexed(this) { terminalSymbolIndex: Int, cykModel: CYKModel, terminalSymbol: TerminalSymbol ->
        cykModel.findProductionRulesForTerminalSymbol(terminalSymbol, terminalSymbolIndex)
    }
}

private fun CYKModel.findProductionRulesForTerminalSymbol(
    terminalSymbol: TerminalSymbol,
    terminalSymbolIndex: Int,
): CYKModel {
    return grammar.productionRuleSet.terminatingRules.fold(this) { cykModel: CYKModel, terminatingRule: TerminatingRule ->
        when {
            terminatingRule produces terminalSymbol -> cykModel.withSymbolAt(terminatingRule.input, 0, terminalSymbolIndex)
            else -> cykModel
        }
    }
}

private fun CYKModel.propagateNonTerminalProductionRules(l: Int): CYKModel {
    return (1..(inputString.count() - l + 1)).fold(this) { rowModel: CYKModel, s: Int ->
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
           model.allowsNonTerminalRuleAt(rule, l = l, s = s, p = p) -> model.withSymbolAt(rule.input, l - 1, s - 1)
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
    return this.containsSymbolAt(nonTerminatingRule.firstNonTerminatingSymbol, p - 1, s - 1)
            && this.containsSymbolAt(nonTerminatingRule.secondNonTerminatingSymbol, l - p - 1, s + p - 1)
}
