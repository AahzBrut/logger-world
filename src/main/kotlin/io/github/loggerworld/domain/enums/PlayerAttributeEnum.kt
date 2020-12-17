package io.github.loggerworld.domain.enums

enum class PlayerAttributeEnum(
    val isEditable: Boolean
) {
    NOTHING(false),
    POINTS_ON_LEVELUP(false),
    UNALLOCATED_POINTS(false),
    STR(true),
    INT(true),
    CON(true),
    AGI(true);

    companion object {
        fun getById(id: Byte): PlayerAttributeEnum =
            values()[id.toInt()]
    }

}