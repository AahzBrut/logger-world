package io.github.loggerworld.mapper.monster

import io.github.loggerworld.domain.monster.MonsterClass
import io.github.loggerworld.dto.inner.monster.MonsterClassSpawnerData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class MonsterClassSpawnerDataMapper(
    private val mapper: MonsterLevelSpawnerDataMapper,
): Mapper<MonsterClassSpawnerData, MonsterClass> {


    override fun fromList(source: List<MonsterClass>): MonsterClassSpawnerData {
        val spawnerData = MonsterClassSpawnerData()

        source.forEach { monsterClass ->
            spawnerData.classes[monsterClass.code] = mapper.from(monsterClass)
        }

        return spawnerData
    }
}