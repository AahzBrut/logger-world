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

        var prevValue = .0
        source.stats.sortedByDescending { it.probability }.forEach { monsterSpawnerType ->
            val currentValue = (monsterSpawnerType.probability / 100.0)
            monsterTypeSpawnerData.probabilities[monsterSpawnerType.monsterType.code] = Pair(prevValue, prevValue + currentValue)
            monsterTypeSpawnerData.types[monsterSpawnerType.monsterType.code] = monsterSpawnerDataMapper.from(monsterSpawnerType)
            prevValue += currentValue
        }

        return monsterTypeSpawnerData
    }
}