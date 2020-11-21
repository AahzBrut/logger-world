package io.github.loggerworld.domain.enums

enum class Languages {
    EN, RU;

    companion object {
        fun getById(id: Byte): Languages =
            values()[id.toInt()]
    }
}