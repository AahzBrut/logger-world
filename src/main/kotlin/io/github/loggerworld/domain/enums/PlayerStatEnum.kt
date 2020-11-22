package io.github.loggerworld.domain.enums

enum class PlayerStatEnum {
    NOTHING, HP, MP, DEF, ATK, SPD, CRT, EVS, STR, INT, CON, AGI;

    companion object {
        fun getById(id: Byte): PlayerStatEnum =
            values()[id.toInt()]
    }
}
