package io.github.loggerworld.domain.enums

enum class LogTypes(
    val logClass: LogClasses
) {
    LOGIN(LogClasses.SYSTEM),
    LOGOFF(LogClasses.SYSTEM),
    DEPARTURE(LogClasses.MOVEMENT),
    ARRIVAL(LogClasses.MOVEMENT),
    NEST_KICKED(LogClasses.COMBAT),
    ATTACKED_BY_MOB(LogClasses.COMBAT),
    ATTACK_MOB(LogClasses.COMBAT),
    DEAL_DAMAGE_MOB(LogClasses.COMBAT),
    RECEIVE_DAMAGE_MOB(LogClasses.COMBAT),
}