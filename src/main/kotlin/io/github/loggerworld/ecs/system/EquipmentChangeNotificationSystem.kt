package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.EquipmentChangeComponent
import io.github.loggerworld.ecs.component.EquipmentComponent
import io.github.loggerworld.ecs.component.ItemComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.EquipmentChangedEvent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class EquipmentChangeNotificationSystem(
    private val equipmentChangedEventBus: EventBus<EquipmentChangedEvent>,
) : IteratingSystem(allOf(EquipmentChangeComponent::class).get(), EngineSystems.EQUIPMENT_CHANGE_NOTIFICATION_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(player: Entity, deltaTime: Float) {
        val playerComp = player[PlayerComponent.mapper]!!
        val equipmentComp = player[EquipmentComponent.mapper]!!
        equipmentChangedEventBus.dispatchEvent { event ->
            event.playerId = playerComp.playerId
            event.slots = equipmentComp.slots.map {
            val itemComp = it.value[ItemComponent.mapper]!!
                it.key to ItemData(
                    itemComp.id,
                    itemComp.category,
                    itemComp.quality,
                    itemComp.quantity,
                    itemComp.stats,
                    itemComp.stackable
                )
            }.toMap()
            player.remove(EquipmentChangeComponent::class.java)
        }
    }
}