package eu.yeger.cyk.parser

private const val terminalSymbolRegexString = "[a-z]+"
private const val nonTerminalSymbolRegexString = "[A-Z]+[a-z]*"
private const val componentRegexString = "[a-zA-Z]+"

public val terminalSymbolRegex: Regex = terminalSymbolRegexString.toRegex()
public val nonTerminalSymbolRegex: Regex = nonTerminalSymbolRegexString.toRegex()

public val productionRuleRegex: Regex = "$componentRegexString\\s+->(\\s+$componentRegexString)+".toRegex()
