package eu.yeger.cyk

import eu.yeger.cyk.model.TerminalSymbol

internal const val epsilon: String = "ε"

public fun word(word: String): Word {
    return word
        .split(" ")
        .map { TerminalSymbol(it) }
        .asSequence()
}
