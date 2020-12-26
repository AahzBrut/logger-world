package io.github.loggerworld.service

import io.github.loggerworld.service.domain.EquipmentDomainService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class EquipmentService(
    private val equipmentDomainService: EquipmentDomainService,
) : LogAware {

    @Suppress("kotlin:S1144")
    @PostConstruct
    private fun initCache(){
        equipmentDomainService.initEquipmentSlotTypeDescriptions()
        logger().debug("Equipment type descriptions loaded into cache.")
    }
}