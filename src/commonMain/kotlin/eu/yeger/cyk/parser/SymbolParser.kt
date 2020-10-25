package eu.yeger.cyk.parser

import eu.yeger.cyk.Result
import eu.yeger.cyk.asSuccess
import eu.yeger.cyk.fail
import eu.yeger.cyk.model.*

internal fun String.parseTerminalSymbol(): Result<TerminalSymbol> {
    return when {
        this matches terminalSymbolRegex -> TerminalSymbol(this).asSuccess()
        this matches nonTerminalSymbolRegex -> fail("Terminal symbols must be entirely lowercase. ($this)")
        this.isBlank() -> fail("Terminal symbol cannot be blank.")
        else -> fail("Invalid terminal symbol. ($this)")
    }
}

internal fun String.parseNonTerminalSymbol(): Result<NonTerminalSymbol> {
    return when {
        this matches nonTerminalSymbolRegex -> RegularNonTerminalSymbol(this).asSuccess()
        this matches terminalSymbolRegex -> fail("Non terminal symbols must start with an uppercase letter. ($this)")
        this.isBlank() -> fail("Non terminal symbol cannot be blank.")
        else -> fail("Invalid non terminal symbol. ($this)")
    }
}

internal fun String.parseStartSymbol(): Result<StartSymbol> {
    return when {
        this matches nonTerminalSymbolRegex -> StartSymbol(this).asSuccess()
        this matches terminalSymbolRegex -> fail("Start symbols must start with an uppercase letter. ($this)")
        this.isBlank() -> fail("Start symbol cannot be blank.")
        else -> fail("Invalid start symbol. ($this)")
    }
}
