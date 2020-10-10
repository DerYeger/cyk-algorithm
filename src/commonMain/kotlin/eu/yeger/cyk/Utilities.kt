package eu.yeger.cyk

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

const val epsilon: String = "ε"

infix fun <V, E, T> Result<V, E>.and(result: Result<T, E>): Result<T, E> {
    return when (this) {
        is Ok -> result
        is Err -> this
    }
}

fun word(word: String): Sequence<TerminalSymbol> {
    return word
        .split(" ")
        .map { RegularTerminalSymbol(it) }
        .asSequence()
}


fun word(block: () -> String): Sequence<TerminalSymbol> {
    return word(block())
}

