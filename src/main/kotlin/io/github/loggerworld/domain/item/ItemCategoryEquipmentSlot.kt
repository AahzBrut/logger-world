package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.equipment.EquipmentSlotType
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "item_category_equipment_slot")
@AttributeOverride(name = "id", column = Column(name = "item_category_equipment_slot_id"))
data class ItemCategoryEquipmentSlot(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    var itemCategory: ItemCategory = ItemCategory(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_slot_type_id")
    var equipmentSlotType: EquipmentSlotType = EquipmentSlotType()

) : BaseEntity<Short>()
