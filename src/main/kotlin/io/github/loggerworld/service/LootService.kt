package io.github.loggerworld.service

import io.github.loggerworld.service.domain.LootDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LootService(
    private val lootDomainService: LootDomainService
) : LogAware {

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache(){
        lootDomainService.initLootMobs()
        logger().debug("Loot settings are cached")
    }
}