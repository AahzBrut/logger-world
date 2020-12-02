package io.github.loggerworld.service

import io.github.loggerworld.service.domain.MonsterDomainService
import org.springframework.stereotype.Service

@Service
class MonsterService(
    private val monsterDomainService: MonsterDomainService
) {


}