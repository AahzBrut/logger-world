package io.github.loggerworld.domain.enums

enum class CharacterClasses {
    DUMMY, WARRIOR, ARCHER, WIZARD, ASSASSIN;

    companion object {
        fun getById(id: Byte): CharacterClasses =
            values()[id.toInt()]
    }
}
