package eu.yeger.cyk.model

public data class Coordinates(val row: Int, val column: Int)

public sealed class CYKState {
    public abstract val model: CYKModel

    public data class Start(
        public override val model: CYKModel,
    ) : CYKState()

    public data class Step(
        public override val model: CYKModel,
        public val productionRule: ProductionRule,
        public val productionRuleWasApplied: Boolean,
        public val sourceCoordinates: Coordinates,
        public val targetCoordinates: List<Coordinates>,
    ) : CYKState()

    public data class Done(
        public override val model: CYKModel,
        public val wordIsMemberOfLanguage: Boolean,
    ) : CYKState()
}

internal fun CYKState.stepWithRuleAt(
    productionRule: ProductionRule,
    rowIndex: Int,
    columnIndex: Int,
    targetCoordinates: List<Coordinates>,
): CYKState.Step {
    return CYKState.Step(
        model = model.withSymbolAt(productionRule.left, rowIndex = rowIndex, columnIndex = columnIndex),
        productionRule = productionRule,
        productionRuleWasApplied = true,
        sourceCoordinates = Coordinates(rowIndex, columnIndex),
        targetCoordinates = targetCoordinates,
    )
}

internal fun CYKState.stepWithoutRuleAt(
    productionRule: ProductionRule,
    rowIndex: Int,
    columnIndex: Int,
    targetCoordinates: List<Coordinates>,
): CYKState.Step {
    return CYKState.Step(
        model = model,
        productionRule = productionRule,
        productionRuleWasApplied = false,
        sourceCoordinates = Coordinates(rowIndex, columnIndex),
        targetCoordinates = targetCoordinates,
    )
}

internal fun CYKState.terminated(): CYKState.Done {
    return CYKState.Done(
        model = model,
        wordIsMemberOfLanguage = model.result,
    )
}
