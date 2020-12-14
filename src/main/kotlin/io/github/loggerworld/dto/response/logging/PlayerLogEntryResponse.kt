package io.github.loggerworld.dto.response.logging

import io.github.loggerworld.domain.enums.LogClasses
import io.github.loggerworld.domain.enums.LogTypes
import java.time.OffsetDateTime

data class PlayerLogEntryResponse(
    val dateTime: OffsetDateTime = OffsetDateTime.now(),
    val logClass: LogClasses = LogClasses.SYSTEM,
    val logType: LogTypes = LogTypes.LOGIN,
    var message: String = ""
)
