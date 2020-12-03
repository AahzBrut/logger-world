package io.github.loggerworld.service.domain

import io.github.loggerworld.dto.inner.monster.MonsterClassSpawnerData
import io.github.loggerworld.mapper.monster.MonsterClassSpawnerDataMapper
import io.github.loggerworld.repository.monster.MonsterClassRepository
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
class MonsterDomainService(
    private val monsterClassRepository: MonsterClassRepository,
    private val spawnerMapper: MonsterClassSpawnerDataMapper,
) {

    @Transactional
    fun getAllMonsterClassesSpawners(): MonsterClassSpawnerData {
        val monsterClasses = monsterClassRepository.findAll()
        return spawnerMapper.fromList(monsterClasses)
    }
}
