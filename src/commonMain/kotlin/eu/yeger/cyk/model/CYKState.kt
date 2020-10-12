package eu.yeger.cyk.model

import eu.yeger.cyk.Word
import eu.yeger.cyk.epsilon

public data class Coordinates(val row: Int, val column: Int)

public sealed class CYKState {
    public abstract val cykModel: CYKModel
}

public data class CYKStart(
    public override val cykModel: CYKModel,
) : CYKState()

public data class CYKStep(
    public override val cykModel: CYKModel,
    public val productionRule: ProductionRule,
    public val ruleWasApplied: Boolean,
    public val sourceCoordinates: Coordinates,
    public val targetCoordinates: List<Coordinates>
) : CYKState()

internal fun CYKState.stepWithRuleAt(
    productionRule: ProductionRule,
    rowIndex: Int,
    columnIndex: Int,
    targetCoordinates: List<Coordinates>,
): CYKStep {
    return CYKStep(
        cykModel = cykModel.withSymbolAt(productionRule.left, rowIndex = rowIndex, columnIndex = columnIndex),
        productionRule = productionRule,
        ruleWasApplied = true,
        sourceCoordinates = Coordinates(rowIndex, columnIndex),
        targetCoordinates = targetCoordinates,
    )
}

internal fun CYKState.stepWithoutRuleAt(
    productionRule: ProductionRule,
    rowIndex: Int,
    columnIndex: Int,
    targetCoordinates: List<Coordinates>,
): CYKStep {
    return CYKStep(
        cykModel = cykModel,
        productionRule = productionRule,
        ruleWasApplied = false,
        sourceCoordinates = Coordinates(rowIndex, columnIndex),
        targetCoordinates = targetCoordinates,
    )
}

internal fun emptyProductionRuleCYKStates(
    word: Word,
    grammar: Grammar,
): List<CYKState> {
    val start = CYKStart(CYKModel(word, grammar))
    val step =  start.stepWithRuleAt(
        productionRule = TerminatingRule(grammar.startSymbol, TerminalSymbol(epsilon)),
        rowIndex = 0,
        columnIndex = 0,
        targetCoordinates = listOf(Coordinates(-1, 0))
    )
    return listOf(start, step)
}
