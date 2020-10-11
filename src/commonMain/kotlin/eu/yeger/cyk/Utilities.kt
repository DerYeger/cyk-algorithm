package eu.yeger.cyk

const val epsilon: String = "ε"

fun word(word: String): Word {
    return word
        .split(" ")
        .map { RegularTerminalSymbol(it) }
        .asSequence()
}
