package io.github.loggerworld.domain.enums

enum class PlayerClasses {
    DUMMY, WARRIOR, ARCHER, WIZARD, ASSASSIN;

    companion object {
        fun getById(id: Byte): PlayerClasses =
            values()[id.toInt()]
    }
}
