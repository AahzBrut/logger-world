package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.EquipmentSlotTypes
import ktx.ashley.mapperFor

class EquipmentComponent : Component, Pool.Poolable {

    var equipmentSlots: MutableMap<EquipmentSlotTypes, Entity> = mutableMapOf()

    override fun reset() = Unit

    companion object {
        val mapper = mapperFor<EquipmentComponent>()
    }
}