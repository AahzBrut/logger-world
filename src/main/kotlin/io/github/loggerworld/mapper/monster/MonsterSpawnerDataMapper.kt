package io.github.loggerworld.mapper.monster

import io.github.loggerworld.domain.monster.MonsterSpawnerType
import io.github.loggerworld.dto.inner.monster.MonsterSpawnerData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class MonsterSpawnerDataMapper : Mapper<MonsterSpawnerData, MonsterSpawnerType> {

    override fun from(source: MonsterSpawnerType): MonsterSpawnerData {
        val monsterSpawnerData = MonsterSpawnerData()

        source.stats.forEach { monsterStat ->
            monsterSpawnerData.stats[monsterStat.playerStat.code] = monsterStat.value
        }

        return monsterSpawnerData
    }
}