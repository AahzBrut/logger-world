package io.github.loggerworld.dto.inner.logging

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes

data class LoggingData(
    var classes: MutableMap<LogClasses, LogTypesData> = mutableMapOf()
)

data class LogTypesData(
    var types: MutableMap<LogTypes, LogTypeData> = mutableMapOf()
)

data class LogTypeData(
    var numValues: Byte = 0,
    var templates: MutableMap<Byte, MessageTemplateData> = mutableMapOf()
)

data class MessageTemplateData(
    var messageId: Int = 0,
    var messages: MutableMap<Languages, String> = mutableMapOf()
)