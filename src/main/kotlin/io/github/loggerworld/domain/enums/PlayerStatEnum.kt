package io.github.loggerworld.domain.enums

@Suppress("UNUSED")
enum class PlayerStatEnum {
    NOTHING,
    HP,
    MP,
    DEF,
    ATK,
    SPD,
    CRT,
    EVS,
    LEVEL;

    companion object {
        fun getById(id: Byte): PlayerStatEnum =
            values()[id.toInt()]
    }
}
