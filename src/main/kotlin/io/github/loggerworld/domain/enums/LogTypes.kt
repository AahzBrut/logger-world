package io.github.loggerworld.domain.enums

enum class LogTypes(
    val logClass: LogClasses
) {
    LOGIN(LogClasses.SYSTEM),
    LOGOFF(LogClasses.SYSTEM),
    DEPARTURE(LogClasses.MOVEMENT),
    ARRIVAL(LogClasses.MOVEMENT)
}