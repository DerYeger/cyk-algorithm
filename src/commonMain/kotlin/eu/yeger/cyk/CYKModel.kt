package eu.yeger.cyk

data class CYKModel(val array: Array<Array<MutableSet<NonTerminalSymbol>>>) {

    constructor(wordLength: Int) : this(Array(wordLength) { rowIndex ->
        Array(wordLength - rowIndex) {
            mutableSetOf<NonTerminalSymbol>()
        }
    })

    operator fun get(rowIndex: Int, columnIndex: Int): Set<NonTerminalSymbol> {
        return array[rowIndex][columnIndex]
    }

    fun add(rowIndex: Int, columnIndex: Int, nonTerminalSymbol: NonTerminalSymbol) {
        array[rowIndex][columnIndex].add(nonTerminalSymbol)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CYKModel

        if (!array.contentDeepEquals(other.array)) return false

        return true
    }

    override fun hashCode(): Int {
        return array.contentDeepHashCode()
    }
}

fun CYKModel.isTrue(): Boolean {
    return array.last().first().any { it is StartSymbol}
}

fun CYKModel.toFormattedString(): String {
    return with(array) {
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
