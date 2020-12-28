package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class CombatEventTypes {
    NONE,
    ATTACK_MOB,
    ATTACK_PLAYER,
    ATTACKED_BY_MOB,
    ATTACKED_BY_PLAYER,
    DEAL_DAMAGE_MOB,
    DEAL_DAMAGE_PLAYER,
    RECEIVE_DAMAGE_MOB,
    RECEIVE_DAMAGE_PLAYER,
    MOB_DEAD,
    PLAYER_DEAD,
    DEATH_FROM_MOB,
    DEATH_FROM_PLAYER;

    @JsonValue
    fun jsonValue() = ordinal
}