package eu.yeger.cyk.model

import eu.yeger.cyk.epsilon

public data class CYKModel
internal constructor(
    val word: Word,
    val grammar: Grammar,
    val grid: List<List<Set<NonTerminalSymbol>>>,
) {
    internal constructor(
        word: Word,
        grammar: Grammar,
    ) : this(
        word,
        grammar,
        List(word.size) { rowIndex ->
            List(word.size - rowIndex) {
                setOf<NonTerminalSymbol>()
            }
        },
    )

    override fun toString(): String {
        return with(grid) {
            val maxRuleSetLength = maxOf { row ->
                row.maxOf { set ->
                    set.joinToString(", ").length
                }
            }
            val maxTerminalSymbolLength = word.maxOf { terminalSymbol ->
                terminalSymbol.toString().length
            }.coerceAtLeast(epsilon.length)
            val maxLength = maxOf(maxRuleSetLength, maxTerminalSymbolLength)

            val columnPadding = " | "
            val wordRowString = word.joinToString(columnPadding, prefix = "| ", postfix = " |") { nonTerminalSymbol ->
                when {
                    nonTerminalSymbol.symbol.isEmpty() -> epsilon
                    else -> nonTerminalSymbol.toString()
                }.padEnd(maxLength, ' ')
            }.plus("\n")
            val wordRowSeparator = "".padEnd(size * maxLength + (size - 1) * columnPadding.length + 4, '-').plus("\n")

            wordRowSeparator + wordRowString + wordRowSeparator.repeat(2) + joinToString(separator = "") { row ->
                row.joinToString(columnPadding, prefix = "| ", postfix = " |") { set ->
                    set.joinToString(", ") { it.toString() }.padEnd(maxLength, ' ')
                }.plus("\n")
                    .plus("-".repeat(row.size * maxLength + (row.size - 1) * columnPadding.length + 4))
                    .plus("\n")
            }
        }
    }
}

public val CYKModel.result: Boolean
    get() = grid.last().first().contains(grammar.startSymbol)

public operator fun CYKModel.get(rowIndex: Int, columnIndex: Int): Set<NonTerminalSymbol> {
    return grid[rowIndex][columnIndex]
}

public fun CYKModel.containsSymbolAt(nonTerminalSymbol: NonTerminalSymbol, rowIndex: Int, columnIndex: Int): Boolean {
    return get(rowIndex, columnIndex).contains(nonTerminalSymbol)
}

internal fun CYKModel.allowsNonTerminalRuleAt(
    nonTerminatingRule: NonTerminatingRule,
    l: Int,
    s: Int,
    p: Int,
): Boolean {
    return this.containsSymbolAt(nonTerminatingRule.firstRight, p - 1, s - 1) &&
        this.containsSymbolAt(nonTerminatingRule.secondRight, l - p - 1, s + p - 1)
}

internal fun CYKModel.withSymbolAt(nonTerminalSymbol: NonTerminalSymbol, rowIndex: Int, columnIndex: Int): CYKModel {
    return grid.mapIndexed { gridRowIndex, gridRow ->
        when (gridRowIndex) {
            rowIndex -> gridRow.mapIndexed { gridColumnIndex, nonTerminalSymbolSet ->
                when (gridColumnIndex) {
                    columnIndex -> nonTerminalSymbolSet + nonTerminalSymbol
                    else -> nonTerminalSymbolSet
                }
            }
            else -> gridRow
        }
    }.let { newGrid -> copy(grid = newGrid) }
}
