package eu.yeger.cyk

const val epsilon: String = "ε"

fun word(word: String): Sequence<TerminalSymbol> {
    return word
        .split(" ")
        .map { RegularTerminalSymbol(it) }
        .asSequence()
}

fun word(block: () -> String): Sequence<TerminalSymbol> {
    return word(block())
}
