package io.github.loggerworld.dto.inner.logging

import io.github.loggerworld.domain.enums.LogValueTypes

data class LogValueData(
    var ordinal: Byte,
    var valueType: LogValueTypes,
    var value: String
)
