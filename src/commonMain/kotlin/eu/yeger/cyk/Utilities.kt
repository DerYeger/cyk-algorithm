package eu.yeger.cyk

import eu.yeger.cyk.model.RegularTerminalSymbol

const val epsilon: String = "ε"

fun word(word: String): Word {
    return word
        .split(" ")
        .map { RegularTerminalSymbol(it) }
        .asSequence()
}
