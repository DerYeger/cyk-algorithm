package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    val n = inputString.count()
    val array = Array(n) { rowIndex ->
        Array(n - rowIndex) {
            mutableSetOf<NonTerminalSymbol>()
        }
    }

    inputString.forEachIndexed { terminalSymbolIndex, terminalSymbol ->
        grammar.productionsRules.terminatingRules.forEach { productionRule ->
            if (productionRule produces terminalSymbol) {
                array[0][terminalSymbolIndex].add(productionRule.input)
            }
        }
    }

    for (l in 2..n) {
        for (s in 1..(n - l + 1)) {
            for (p in 1 until l) {
                grammar.productionsRules.nonTerminatingRules.forEach { productionRule ->
                    if (
                        array[p-1][s-1].contains(productionRule.firstNonTerminatingSymbol)
                        && array[l - p - 1][s + p - 1].contains(productionRule.secondNonTerminatingSymbol)
                    ) {
                        array[l-1][s-1].add(productionRule.input)
                    }
                }
            }
        }
    }

    println(array.toFormattedString())

    return array[n - 1][0].contains(grammar.startSymbol)
}

fun Array<Array<MutableSet<NonTerminalSymbol>>>.toFormattedString(): String {
    val maxLength = maxOf { row ->
        row.maxOf { set ->
            set.joinToString(", ") { it.toString() }.length
        }
    }
    val columnPadding = " | "
    val firstRowString = "\n".padEnd(size * maxLength + (size - 1) * columnPadding.length + 5, '-').plus("\n")
    return firstRowString + joinToString(separator = "") { row ->
        row.joinToString(columnPadding, prefix = "| ", postfix = " |") { set ->
            set.joinToString(", ") { it.toString() }.padEnd(maxLength, ' ')
        }.plus("\n")
            .plus("-".repeat(row.size * maxLength + (row.size - 1) * columnPadding.length + 4))
            .plus("\n")
    }
}
