package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import io.github.loggerworld.dto.response.Description
import io.github.loggerworld.repository.equipment.EquipmentSlotTypeDescriptionRepository
import io.github.loggerworld.util.LogAware
import org.springframework.stereotype.Service
import javax.transaction.Transactional

typealias EquipmentSlotTypeDescriptions = MutableMap<EquipmentSlotTypes, LanguageDescriptions>

@Service
class EquipmentDomainService(
    private val equipmentSlotTypeDescriptionRepository: EquipmentSlotTypeDescriptionRepository
) : LogAware {

    val equipmentSlotTypeDescriptions: EquipmentSlotTypeDescriptions = mutableMapOf()

    @Transactional
    fun initEquipmentSlotTypeDescriptions(){
        equipmentSlotTypeDescriptions.clear()
        equipmentSlotTypeDescriptionRepository.findAll()
            .forEach {
                equipmentSlotTypeDescriptions.computeIfAbsent(it.equipmentSlotType.code) { mutableMapOf() }
                equipmentSlotTypeDescriptions[it.equipmentSlotType.code]!![it.language.code] = Description(it.name, it.description)
            }
    }
}
