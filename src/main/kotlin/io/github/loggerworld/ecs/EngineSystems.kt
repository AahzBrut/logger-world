package io.github.loggerworld.ecs


// To visually sort systems priorities
enum class EngineSystems {
    LOCATION_MAP_SYSTEM,
    PLAYER_MAP_SYSTEM,
    PLAYER_MOVE_COMMAND_SYSTEM,
    PLAYER_SPAWN_SYSTEM,
    MOVE_SYSTEM,
    MONSTER_RESPAWN_SYSTEM,
    PLAYER_KICK_MONSTER_NEST_SYSTEM,
    LOCATION_INHABITANT_ALERT_SYSTEM,
}