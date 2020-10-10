package eu.yeger.cyk

class CYKModel
private constructor(val grid: List<List<Set<NonTerminalSymbol>>>) {

    constructor(wordLength: Int) : this(List(wordLength) { rowIndex ->
        List(wordLength - rowIndex) {
            setOf<NonTerminalSymbol>()
        }
    })

    operator fun get(rowIndex: Int, columnIndex: Int): Set<NonTerminalSymbol> {
        return grid[rowIndex][columnIndex]
    }

    fun add(rowIndex: Int, columnIndex: Int, nonTerminalSymbol: NonTerminalSymbol): CYKModel {
        val newGrid = grid.mapIndexed { gridRowIndex, row ->
            when (gridRowIndex) {
                rowIndex -> row.mapIndexed { gridColumnIndex, set ->
                    when (gridColumnIndex) {
                        columnIndex -> set + nonTerminalSymbol
                        else -> set
                    }
                }
                else -> row
            }
        }
        return CYKModel(newGrid)
    }
}

fun CYKModel.isTrue(): Boolean {
    return grid.last().first().any { it is StartSymbol }
}

fun CYKModel.toFormattedString(): String {
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
