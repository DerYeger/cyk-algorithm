package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    val n = inputString.count()
    var model = CYKModel(inputString.count())

    inputString.forEachIndexed { terminalSymbolIndex, terminalSymbol ->
        grammar.productionsRules.terminatingRules.forEach { productionRule ->
            if (productionRule produces terminalSymbol) {
                model = model.add(0, terminalSymbolIndex, productionRule.input)
            }
        }
    }

    for (l in 2..n) {
        for (s in 1..(n - l + 1)) {
            for (p in 1 until l) {
                grammar.productionsRules.nonTerminatingRules.forEach { productionRule ->
                    if (
                        model[p-1, s-1].contains(productionRule.firstNonTerminatingSymbol)
                        && model[l - p - 1, s + p - 1].contains(productionRule.secondNonTerminatingSymbol)
                    ) {
                        model = model.add(l-1, s-1, productionRule.input)
                    }
                }
            }
        }
    }

    println(model.toFormattedString())

    return model.isTrue()
}
