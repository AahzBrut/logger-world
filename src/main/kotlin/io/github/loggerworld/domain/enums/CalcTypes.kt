package io.github.loggerworld.domain.enums


val statCalculators: Map<CalcTypes, (Float, Float, Float) -> Float> =
    mapOf(
        CalcTypes.ADD_MUL_COEFF to ::calcAddMulCoeff
    )

enum class CalcTypes {
    ADD_MUL_COEFF
}

@Suppress("unused_parameter")
fun calcAddMulCoeff(statValue: Float, attributeValue: Float, coeff: Float) : Float {
    return attributeValue * coeff
}