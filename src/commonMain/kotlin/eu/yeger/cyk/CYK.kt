package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    if (grammar.validatedOrNull() == null) return false

    val n = inputString.count()
    val array = Array(n) {
        Array(n) {
            mutableSetOf<NonTerminalSymbol>()
        }
    }

    inputString.forEachIndexed { terminalSymbolIndex, terminalSymbol ->
        grammar.productionsRules.forEach { productionRule ->
            if (productionRule is TerminatingRule && productionRule produces terminalSymbol) {
                array[0][terminalSymbolIndex].add(productionRule.input)
            }
        }
    }

    val nonTerminatingRules = grammar.productionsRules.filterIsInstance<NonTerminatingRule>()

    for (l in 2..n) {
        for (s in 1..(n-l+1)) {
            for (p in 1 until l) {
                nonTerminatingRules.forEach { productionRule ->
                    if (array[p-1][s-1].contains(productionRule.firstNonTerminatingSymbol) && array[l-p-1][s+p-1].contains(productionRule.secondNonTerminatingSymbol)) {
                        array[l-1][s-1].add(productionRule.input)
                    }
                }
            }
        }
    }

    println(array.toFormattedString())

    return array[n-1][0].contains(grammar.startSymbol)
}

fun Array<Array<MutableSet<NonTerminalSymbol>>>.toFormattedString(): String {
    val maxLength = maxOf { row ->
        row.maxOf { set ->
            set.joinToString(", ") { it.toString() }.length
        }
    }
    return joinToString("\n") { row ->
        row.joinToString(" | ") { set ->
            set.joinToString(", ") { it.toString() }.padEnd(maxLength, ' ')
        }
    }
}
