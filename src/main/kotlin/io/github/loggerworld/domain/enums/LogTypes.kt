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
    PLAYER_KILLED_BY_MOB(LogClasses.COMBAT),
    MOB_KILLED(LogClasses.COMBAT),
    PLAYER_GOT_ITEM(LogClasses.LOOT),
}