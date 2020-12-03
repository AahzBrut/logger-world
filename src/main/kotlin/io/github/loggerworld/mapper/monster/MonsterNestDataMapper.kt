package io.github.loggerworld.mapper.monster

import io.github.loggerworld.domain.geography.LocationMonsterSpawner
import io.github.loggerworld.dto.inner.monster.MonsterNestData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class MonsterNestDataMapper: Mapper<MonsterNestData, LocationMonsterSpawner> {

    override fun from(source: LocationMonsterSpawner): MonsterNestData {

        return MonsterNestData(
            source.id!!,
            source.monsterSpawner.monsterClass.code,
            source.monsterSpawner.level,
            source.amount,
            source.minRespawnTime,
            source.maxRespawnTime
        )
    }
}