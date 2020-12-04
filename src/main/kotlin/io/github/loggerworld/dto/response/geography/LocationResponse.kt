package io.github.loggerworld.dto.response.geography

import io.github.loggerworld.dto.response.monster.MobNestResponse

data class LocationResponse(
    var id: Short = -1,
    var typeId: Byte = -1,
    var xCoord: Byte = -1,
    var yCoord: Byte = -1,
    var name: String = "",
    var description: String = "",
    var nests: List<MobNestResponse> = mutableListOf()

)
