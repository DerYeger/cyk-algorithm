package eu.yeger.cyk

fun cyk(grammar: Grammar, inputString: Sequence<TerminalSymbol>): Boolean {
    if (grammar.validatedOrNull() == null) return false

    val n = inputString.count()
    val map = MutableTripleMap<Int, Int, NonTerminalSymbol, Boolean>()

    inputString.forEachIndexed { terminalSymbolIndex, terminalSymbol ->
        grammar.productionsRules.forEach { productionRule ->
            map[0, terminalSymbolIndex, productionRule.input] = productionRule is TerminatingRule && productionRule produces terminalSymbol
            if (terminalSymbol == CustomTerminalSymbol("a") && productionRule is TerminatingRule && productionRule produces terminalSymbol) {
                println("Rule: $productionRule")
                println("Produces: ${productionRule is TerminatingRule && productionRule produces terminalSymbol}")
                println(terminalSymbolIndex)
                println(map[0, terminalSymbolIndex, productionRule.input])
                println()
            }
        }
    }

    val nonTerminatingRules = grammar.productionsRules.filterIsInstance<NonTerminatingRule>()

    for (l in 2..n) {
        for (s in 1..(n-l+1)) {
            for (p in 1 until l) {
                nonTerminatingRules.forEach { productionRule ->
                    if (map.has(p-1, s-1, productionRule.firstNonTerminatingSymbol) && map.has(l-p-1, s+p-1, productionRule.secondNonTerminatingSymbol)) {
                        map[l-1, s-1, productionRule.input] = true
                    }
                }
            }
        }
    }

    println(map.toString(n))

    return map.has(n-1, 0, grammar.startSymbol)
}

fun MutableTripleMap<Int, Int, NonTerminalSymbol, Boolean>.toString(dim: Int): String {
    val array = asArray(dim)
    val maxLength = array.maxOf { row ->
        row.maxOf { set ->
            set.joinToString(", ") { it.toString() }.length
        }
    }
    println(maxLength)
    return array.joinToString("\n") { row ->
        row.joinToString(" | ") { set ->
            set.joinToString(", ") { it.toString() }.padEnd(maxLength, ' ')
        }
    }
}

fun MutableTripleMap<Int, Int, NonTerminalSymbol, Boolean>.asArray(dim: Int): Array<Array<MutableSet<NonTerminalSymbol>>> {
    val array = Array(dim) { Array (dim) { mutableSetOf<NonTerminalSymbol>() } }
    forEach { entry ->
        if (entry.value) {
            val (l, s, symbol) = entry.key
            array[l][s].add(symbol)
        }
    }
    return array
}
