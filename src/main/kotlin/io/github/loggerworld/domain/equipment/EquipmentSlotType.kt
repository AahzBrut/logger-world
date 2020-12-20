package io.github.loggerworld.domain.equipment

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated


@Entity(name = "equipment_slot_type")
@AttributeOverride(name = "id", column = Column(name = "equipment_slot_type_id"))
data class EquipmentSlotType(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: EquipmentSlotTypes = EquipmentSlotTypes.NOTHING,

    ) : BaseEntity<Byte>()
