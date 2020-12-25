package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.InventoryChangedComponent
import io.github.loggerworld.ecs.component.InventoryComponent
import io.github.loggerworld.ecs.component.ItemComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.DeserializeItemsDropFromMobCommand
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.PlayerGotItemEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class LootAcquireSystem(
    private val deserializeCommandBus: EventBus<DeserializeItemsDropFromMobCommand>,
    private val logEventBus: LogEventBus<LogEvent>,
) : EntitySystem(EngineSystems.LOOT_ACQUIRE_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {

        while (deserializeCommandBus.receiveEvent { event ->
                val player = playerMap[event.playerId]
                val playerComp = player[PlayerComponent.mapper]!!
                val inventoryComp = player[InventoryComponent.mapper]!!
                player.addComponent<InventoryChangedComponent>(engine)
                event.items.forEach { item ->
                    if (inventoryComp.currentSize == inventoryComp.maxSize) {
                        dropItemOnTheGround(item)
                    } else {
                        inventoryComp.currentSize++
                        inventoryComp.slots.add(spawnItem(item))
                        logGotItem(playerComp, item)
                        logger().debug("\n\nItem: $item\n was added to inventory of ${playerComp.playerName}")
                    }
                }
            }) {
            // Empty block
        }
    }

    private fun logGotItem(playerComp: PlayerComponent, item: ItemData) {
        val event = logEventBus.newEvent(PlayerGotItemEvent::class) as PlayerGotItemEvent
        event.playerId = playerComp.playerId
        event.itemData = item
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun spawnItem(item: ItemData): Entity {
        return engine.entity {
            with<ItemComponent> {
                this.id = item.id
                this.category = item.category
                this.quality = item.quality
                this.quantity = item.quantity
                this.stats.putAll(item.stats)
                this.stackable = item.stackable
            }
        }
    }

    private fun dropItemOnTheGround(item: ItemData) {
        TODO("Not yet implemented")
    }
}