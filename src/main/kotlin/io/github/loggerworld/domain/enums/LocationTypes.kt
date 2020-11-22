package io.github.loggerworld.domain.enums

enum class LocationTypes {
    VOID,
    IN_TRANSIT,
    CITY,
    VILLAGE,
    WOODS;

    companion object {
        fun getById(id: Byte): LocationTypes =
            values()[id.toInt()]
    }
}