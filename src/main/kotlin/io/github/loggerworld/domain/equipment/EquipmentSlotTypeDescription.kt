package io.github.loggerworld.domain.equipment

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.item.ItemCategory
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne


@Entity(name = "equipment_slot_type_description")
@AttributeOverride(name = "id", column = Column(name = "equipment_slot_type_description_id"))
data class EquipmentSlotTypeDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_slot_type_id")
    var equipmentSlotType: EquipmentSlotType = EquipmentSlotType(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(Languages.EN),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Short>()
