package eu.yeger.cyk.model

public class Word
private constructor(terminalSymbols: List<TerminalSymbol>) : List<TerminalSymbol> by terminalSymbols {
    public constructor(word: String) : this(
        word.split(" ")
            .filter { it.isNotBlank() }
            .map { TerminalSymbol(it) }
            .ifEmpty { listOf(TerminalSymbol("")) }
    )
}
