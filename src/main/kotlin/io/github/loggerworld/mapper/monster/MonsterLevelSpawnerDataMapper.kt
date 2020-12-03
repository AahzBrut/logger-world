package io.github.loggerworld.mapper.monster

import io.github.loggerworld.domain.monster.MonsterClass
import io.github.loggerworld.dto.inner.monster.MonsterLevelSpawnerData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MonsterLevelSpawnerDataMapper(
    private val mapper: MonsterTypeSpawnerDataMapper
) : Mapper<MonsterLevelSpawnerData, MonsterClass>{


    override fun from(source: MonsterClass): MonsterLevelSpawnerData {
        val levelSpawnerData = MonsterLevelSpawnerData()

        source.spawners.forEach { monsterSpawner ->
            levelSpawnerData.levels[monsterSpawner.level] = mapper.from(monsterSpawner)
        }

        return levelSpawnerData
    }
}