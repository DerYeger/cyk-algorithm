package eu.yeger.cyk.parser

import eu.yeger.cyk.Result
import eu.yeger.cyk.map
import eu.yeger.cyk.mapAsResult
import eu.yeger.cyk.model.TerminalSymbol
import eu.yeger.cyk.model.Word

public fun word(wordString: String): Result<Word> {
    return wordString
        .split(" ")
        .filter(String::isNotBlank)
        .parseTerminalSymbols()
        .map(::Word)
}

internal fun List<String>.parseTerminalSymbols(): Result<List<TerminalSymbol>> {
    return mapAsResult(String::parseTerminalSymbol)
        .map { terminalSymbols ->
            terminalSymbols.ifEmpty { listOf(TerminalSymbol("")) }
        }
}
