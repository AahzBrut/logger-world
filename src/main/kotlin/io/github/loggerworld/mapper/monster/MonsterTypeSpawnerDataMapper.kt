package io.github.loggerworld.mapper.monster

import io.github.loggerworld.domain.monster.MonsterSpawner
import io.github.loggerworld.dto.inner.monster.MonsterTypeSpawnerData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class MonsterTypeSpawnerDataMapper(
    private val monsterSpawnerDataMapper: MonsterSpawnerDataMapper
) : Mapper<MonsterTypeSpawnerData, MonsterSpawner> {

    override fun from(source: MonsterSpawner): MonsterTypeSpawnerData {
        val monsterTypeSpawnerData = MonsterTypeSpawnerData()

        source.stats.forEach {monsterSpawnerType ->
            monsterTypeSpawnerData.probabilities[monsterSpawnerType.monsterType.code] = monsterSpawnerType.probability
            monsterTypeSpawnerData.types[monsterSpawnerType.monsterType.code] = monsterSpawnerDataMapper.from(monsterSpawnerType)
        }

        return monsterTypeSpawnerData
    }
}