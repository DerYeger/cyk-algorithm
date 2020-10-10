package eu.yeger.cyk

data class CYKModel(
    val grammar: Grammar,
    val inputString: Sequence<TerminalSymbol>,
    val grid: List<List<Set<NonTerminalSymbol>>>,
) {

    constructor(
        grammar: Grammar,
        inputString: Sequence<TerminalSymbol>,
    ) : this(
        grammar,
        inputString,
        List(inputString.count()) { rowIndex ->
            List(inputString.count() - rowIndex) {
                setOf<NonTerminalSymbol>()
            }
        }
    )

    operator fun get(rowIndex: Int, columnIndex: Int): Set<NonTerminalSymbol> {
        return grid[rowIndex][columnIndex]
    }

    fun withSymbolAt(nonTerminalSymbol: NonTerminalSymbol, rowIndex: Int, columnIndex: Int): CYKModel {
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

    override fun toString(): String {
        return with(grid) {
            val maxLength = maxOf { row ->
                row.maxOf { set ->
                    set.joinToString(", ") { it.toString() }.length
                }
            }
            val columnPadding = " | "
            val firstRowString = "\n".padEnd(size * maxLength + (size - 1) * columnPadding.length + 5, '-').plus("\n")
            firstRowString + joinToString(separator = "") { row ->
                row.joinToString(columnPadding, prefix = "| ", postfix = " |") { set ->
                    set.joinToString(", ") { it.toString() }.padEnd(maxLength, ' ')
                }.plus("\n")
                    .plus("-".repeat(row.size * maxLength + (row.size - 1) * columnPadding.length + 4))
                    .plus("\n")
            }
        }
    }
}

val CYKModel.result: Boolean
    get() = grid.last().first().any { it is StartSymbol }

fun CYKModel.containsSymbolAt(nonTerminalSymbol: NonTerminalSymbol, rowIndex: Int, columnIndex: Int): Boolean {
    return get(rowIndex, columnIndex).contains(nonTerminalSymbol)
}


