package io.github.loggerworld.service

import io.github.loggerworld.dto.inner.monster.MonsterClassSpawnerData
import io.github.loggerworld.dto.inner.monster.MonsterSpawnerData
import io.github.loggerworld.service.domain.MonsterDomainService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MonsterService(
    private val monsterDomainService: MonsterDomainService
) {

    private lateinit var spawnerCache: MonsterClassSpawnerData

    @PostConstruct
    fun initCache() {
        spawnerCache = monsterDomainService.getAllMonsterClassesSpawners()
    }

    fun getMonsterSpawnerData(): MonsterClassSpawnerData =
        spawnerCache
}