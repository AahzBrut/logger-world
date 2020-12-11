package io.github.loggerworld.service

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.dto.inner.monster.MonsterClassSpawnerData
import io.github.loggerworld.dto.inner.monster.MonsterSpawnerData
import io.github.loggerworld.service.domain.LocationDomainService
import io.github.loggerworld.service.domain.MonsterDomainService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MonsterService(
    private val monsterDomainService: MonsterDomainService,
    private val locationDomainService: LocationDomainService
) {

    private lateinit var spawnerCache: MonsterClassSpawnerData

    @PostConstruct
    fun initCache() {
        spawnerCache = monsterDomainService.getAllMonsterClassesSpawners()
    }

    fun getMonsterSpawnerData(): MonsterClassSpawnerData =
        spawnerCache

    fun decodeMonsterNest(nestId: String, language: Languages): String {
        val spawner = locationDomainService.getAllLocations()
            .entries
            .flatMap {
                it.value.monsterNests
            }
            .first {
                it.id == nestId.toShort()
            }

        return "${spawner.monsterClass} (${spawner.level} Lvl)"
    }
}