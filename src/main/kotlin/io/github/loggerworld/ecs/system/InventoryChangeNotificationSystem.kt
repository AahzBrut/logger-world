package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.InventoryChangedComponent
import io.github.loggerworld.ecs.component.InventoryComponent
import io.github.loggerworld.ecs.component.ItemComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.InventoryChangedEvent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class InventoryChangeNotificationSystem(
    private val inventoryChangedEventBus: EventBus<InventoryChangedEvent>,
) : IteratingSystem(allOf(InventoryChangedComponent::class).get(),EngineSystems.LOOT_ACQUIRE_SYSTEM.ordinal), LogAware {

    override fun processEntity(player: Entity, deltaTime: Float) {
        val inventoryComp = player[InventoryComponent.mapper]!!
        val playerComp = player[PlayerComponent.mapper]!!
        inventoryChangedEventBus.dispatchEvent {
            it.playerId = playerComp.playerId
            it.maxSize = inventoryComp.maxSize
            it.currentSize = inventoryComp.currentSize
            it.slots = inventoryComp.slots.map { item ->
                val itemComp = item[ItemComponent.mapper]!!
                ItemData(
                    itemComp.id,
                    itemComp.category,
                    itemComp.quality,
                    itemComp.quantity,
                    itemComp.stats,
                    itemComp.stackable
                )
            }.toList()
        }
        player.remove(InventoryChangedComponent::class.java)
    }

}
