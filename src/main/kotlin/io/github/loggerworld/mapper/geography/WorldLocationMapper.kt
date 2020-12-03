package io.github.loggerworld.mapper.geography

import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.domain.geography.Location
import io.github.loggerworld.dto.inner.WorldLocation
import io.github.loggerworld.mapper.Mapper
import io.github.loggerworld.mapper.monster.MonsterNestDataMapper
import org.springframework.stereotype.Service

@Service
class WorldLocationMapper(
    private val nestMapper: MonsterNestDataMapper
) : Mapper<WorldLocation, Location> {

    override fun from(source: Location) =
        WorldLocation(
            id = source.id!!,
            xCoord = source.xCoord,
            yCoord = source.yCoord,
            type = LocationTypes.getById(source.locationType.id!!),
            monsterNests = nestMapper.from(source.locationMonsterSpawners)
        )
}