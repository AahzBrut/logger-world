package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum.STACK_SIZE
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.InventoryComponent
import io.github.loggerworld.ecs.component.ItemComponent
import io.github.loggerworld.ecs.component.KilledComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.PlayerKillMobEvent
import io.github.loggerworld.messagebus.event.PlayerKilledByMobEvent
import io.github.loggerworld.messagebus.event.SerializeItemsDropFromMobCommand
import io.github.loggerworld.service.ItemService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.hasNot
import ktx.ashley.remove
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GraveyardSystem(
    private val logEventBus: LogEventBus<LogEvent>,
    private val itemService: ItemService,
    private val dropSerializerBus: EventBus<SerializeItemsDropFromMobCommand>
) : IteratingSystem(allOf(KilledComponent::class).get(), EngineSystems.GRAVEYARD_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(deceased: Entity, deltaTime: Float) {
        val killedComp = deceased[KilledComponent.mapper]!!

        if (deceased.has(PlayerComponent.mapper)) {
            val playerComp = deceased[PlayerComponent.mapper]!!
            val monsterComp = killedComp.killer[MonsterComponent.mapper]!!
            logPlayerKilledEvent(playerComp, monsterComp)
            playerComp.location[LocationComponent.mapper]!!.players.remove(deceased)
            if (playerComp.location.hasNot(LocationUpdatedComponent.mapper)) playerComp.location.addComponent<LocationUpdatedComponent>(
                engine
            )
        } else {
            val playerComp = killedComp.killer[PlayerComponent.mapper]!!
            val monsterComp = deceased[MonsterComponent.mapper]!!
            logMonsterKilledEvent(playerComp, monsterComp)
            dropLoot(killedComp.killer, monsterComp, playerComp)
            playerComp.location[LocationComponent.mapper]!!.spawnedMonsters.remove(deceased)
            if (playerComp.location.hasNot(LocationUpdatedComponent.mapper)) playerComp.location.addComponent<LocationUpdatedComponent>(
                engine
            )
        }
        deceased.remove<KilledComponent>()
        deceased.addComponent<RemoveComponent>(engine)
    }

    private fun logPlayerKilledEvent(playerComp: PlayerComponent, monsterComp: MonsterComponent) {
        val event = logEventBus.newEvent(PlayerKilledByMobEvent::class) as PlayerKilledByMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun logMonsterKilledEvent(playerComp: PlayerComponent, monsterComp: MonsterComponent) {
        val event = logEventBus.newEvent(PlayerKillMobEvent::class) as PlayerKillMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun dropLoot(player: Entity, monsterComp: MonsterComponent, playerComp: PlayerComponent) {
        val inventoryComp = player[InventoryComponent.mapper]!!
        val loot = addLootToExistingStacks(getTempLoot(), inventoryComp)
        dropSerializerBus.dispatchEvent {
            it.playerId = playerComp.playerId
            it.monsterClass = monsterComp.monsterClass
            it.monsterType = monsterComp.monsterType
            it.monsterLevel = monsterComp.level
            it.items = loot
        }
    }

    private fun addLootToExistingStacks(loot: List<ItemData>, inventoryComp: InventoryComponent): List<ItemData> {
        loot.forEach { itemData ->
            inventoryComp.slots
                .map {
                    it[ItemComponent.mapper]!!
                }
                .filter {
                    it.stackable && it.category == itemData.category && it.quality == itemData.quality
                }
                .forEach {
                    val stackSize = it.stats[STACK_SIZE]!!.toLong()
                    if (stackSize < 0) {
                        it.quantity += itemData.quantity
                        itemData.quantity = 0
                        logger().debug("\n\nItem ${itemData.category} was added to existing stack")
                    } else {
                        val delta = stackSize - it.quantity
                        if (delta >= itemData.quantity) {
                            it.quantity += itemData.quantity
                            itemData.quantity = 0
                        } else {
                            it.quantity += delta
                            itemData.quantity -= delta
                        }
                    }
                }
        }

        return loot.filter {
            it.quantity > 0
        }
    }

    private fun getTempLoot(): List<ItemData> {
        return listOf(
            itemService.createItem(ItemCategories.SHORT_SWORD, ItemQualities.COMMON, 1, 1),
            itemService.createItem(ItemCategories.GOLD, ItemQualities.COMMON, 1, 10),
        )
    }
}
