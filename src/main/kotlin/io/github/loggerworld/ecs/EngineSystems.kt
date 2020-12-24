package io.github.loggerworld.ecs


// To visually sort systems priorities
enum class EngineSystems {
    LOCATION_MAP_SYSTEM,
    PLAYER_MAP_SYSTEM,
    PLAYER_MOVE_COMMAND_SYSTEM,
    PLAYER_SPAWN_SYSTEM,
    MOVE_SYSTEM,
    MONSTER_RESPAWN_SYSTEM,
    COMBAT_SYSTEM,
    PLAYER_KICK_MONSTER_NEST_SYSTEM,
    GRAVEYARD_SYSTEM,
    LOOT_ACQUIRE_SYSTEM,
    INVENTORY_CHANGE_NOTIFICATION_SYSTEM,
    LOCATION_INHABITANT_ALERT_SYSTEM,
    REMOVE_SYSTEM,
}