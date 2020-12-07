package io.github.loggerworld.dto.response.logging

import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import java.time.LocalDateTime

data class PlayerLogEntryResponse(
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val logClass: LogClasses = LogClasses.SYSTEM,
    val logType: LogTypes = LogTypes.LOGIN,
    val message: String = ""
)
