package io.github.loggerworld.service.domain

import io.github.loggerworld.repository.monster.MonsterClassRepository
import org.springframework.stereotype.Service

@Service
class MonsterDomainService(
    private val monsterClassRepository: MonsterClassRepository
) {
}
