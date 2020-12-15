package io.github.loggerworld.domain.enums

@Suppress("UNUSED")
enum class PlayerStatEnum(
    val isEditable: Boolean
) {
    NOTHING(false),
    HP(false),
    MP(false),
    DEF(false),
    ATK(false),
    SPD(false),
    CRT(false),
    EVS(false),
    STR(true),
    INT(true),
    CON(true),
    AGI(true),
    LEVEL(false),
    POINTS_ON_LEVELUP(false),
    UNALLOCATED_POINTS(false);

    companion object {
        fun getById(id: Byte): PlayerStatEnum =
            values()[id.toInt()]
    }
}
