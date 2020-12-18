package io.github.loggerworld.service

import io.github.loggerworld.service.domain.PlayerAttributeDomainService
import io.github.loggerworld.util.LogAware
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PlayerAttributeService(
    private val playerAttributeDomainService: PlayerAttributeDomainService,
) : LogAware {

    @PostConstruct
    @Suppress("kotlin:S1144")
    private fun initCache() {
        playerAttributeDomainService.setPlayerEffectiveStatData(playerAttributeDomainService.getAllStatsDependencies())
    }

}