package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.domain.enums.CombatEventTypes.DEATH_FROM_MOB
import io.github.loggerworld.domain.enums.CombatEventTypes.MOB_DEAD
import io.github.loggerworld.domain.enums.CombatEventTypes.PLAYER_DEAD
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.ItemQualities
import io.github.loggerworld.domain.enums.ItemStatEnum.STACK_SIZE
import io.github.loggerworld.dto.inner.item.ItemData
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.CombatComponent
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
import io.github.loggerworld.messagebus.event.CombatEvent
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
import ktx.ashley.remove
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GraveyardSystem(
    private val logEventBus: LogEventBus<LogEvent>,
    private val itemService: ItemService,
    private val dropSerializerBus: EventBus<SerializeItemsDropFromMobCommand>,
    private val combatEventBus: EventBus<CombatEvent>,
) : IteratingSystem(allOf(KilledComponent::class).get(), EngineSystems.GRAVEYARD_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(deceased: Entity, deltaTime: Float) {
        val killedComp = deceased[KilledComponent.mapper]!!
        val damageReceived = deceased[CombatComponent.mapper]!!.damageCounters[killedComp.killer]!!
        val damageDealt = killedComp.killer[CombatComponent.mapper]!!.damageCounters[deceased]!!
        deceased[CombatComponent.mapper]!!.damageCounters.remove(killedComp.killer)
        killedComp.killer[CombatComponent.mapper]!!.damageCounters.remove(deceased)


        if (deceased.has(PlayerComponent.mapper)) {
            val playerComp = deceased[PlayerComponent.mapper]!!
            val monsterComp = killedComp.killer[MonsterComponent.mapper]!!
            logPlayerKilledEvent(playerComp, monsterComp, damageReceived, damageDealt)
            playerComp.location[LocationComponent.mapper]!!.players.remove(deceased)
            playerComp.location.addComponent<LocationUpdatedComponent>(engine)
        } else {
            val playerComp = killedComp.killer[PlayerComponent.mapper]!!
            val monsterComp = deceased[MonsterComponent.mapper]!!
            logMonsterKilledEvent(playerComp, monsterComp, damageDealt, damageReceived)
            dropLoot(killedComp.killer, monsterComp, playerComp)
            playerComp.location[LocationComponent.mapper]!!.spawnedMonsters.remove(deceased)
            playerComp.location.addComponent<LocationUpdatedComponent>(engine)
        }
        deceased.remove<KilledComponent>()
        deceased.addComponent<RemoveComponent>(engine)
        syncCombatComponents(deceased, killedComp.killer)
    }

    private fun syncCombatComponents(deceased: Entity, killer: Entity) {
        val deceasedCombatComp = deceased[CombatComponent.mapper]!!
        deceasedCombatComp.enemies.remove(killer)
        deceasedCombatComp.enemies.forEach { enemy ->
            val combatComp = enemy[CombatComponent.mapper]!!
            if (enemy.has(PlayerComponent.mapper)) {
                combatEventBus.dispatchEvent { event ->
                    event.playerId = enemy[PlayerComponent.mapper]!!.playerId
                    event.eventType = if (deceased.has(PlayerComponent.mapper)) PLAYER_DEAD else MOB_DEAD
                    event.enemyId = if (deceased.has(PlayerComponent.mapper)) deceased[PlayerComponent.mapper]!!.playerId else deceased[MonsterComponent.mapper]!!.id
                    event.damage = 0f
                }
            }
            combatComp.enemies.remove(deceased)
            combatComp.damageCounters.remove(deceased)
            if (combatComp.target == deceased) combatComp.target = null
        }
    }

    private fun logPlayerKilledEvent(
        playerComp: PlayerComponent,
        monsterComp: MonsterComponent,
        damageReceived: Float,
        damageDealt: Float,
    ) {
        combatEventBus.dispatchEvent { combatEvent ->
            combatEvent.playerId = playerComp.playerId
            combatEvent.eventType = DEATH_FROM_MOB
            combatEvent.enemyId = monsterComp.id
            combatEvent.damage = 0f
        }

        val event = logEventBus.newEvent(PlayerKilledByMobEvent::class) as PlayerKilledByMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.damageReceived = damageReceived
        event.damageDealt = damageDealt
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun logMonsterKilledEvent(
        playerComp: PlayerComponent,
        monsterComp: MonsterComponent,
        damageReceived: Float,
        damageDealt: Float,
    ) {
        combatEventBus.dispatchEvent { combatEvent ->
            combatEvent.playerId = playerComp.playerId
            combatEvent.eventType = MOB_DEAD
            combatEvent.enemyId = monsterComp.id
            combatEvent.damage = 0f
        }

        val event = logEventBus.newEvent(PlayerKillMobEvent::class) as PlayerKillMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.damageReceived = damageReceived
        event.damageDealt = damageDealt
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun dropLoot(player: Entity, monsterComp: MonsterComponent, playerComp: PlayerComponent) {
        val inventoryComp = player[InventoryComponent.mapper]!!
        val loot = getTempLoot()
        if (loot.isEmpty()) return
        val unstackedLoot = addLootToExistingStacks(loot, inventoryComp)
        dropSerializerBus.dispatchEvent {
            it.playerId = playerComp.playerId
            it.monsterClass = monsterComp.monsterClass
            it.monsterType = monsterComp.monsterType
            it.monsterLevel = monsterComp.level
            it.items = unstackedLoot
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
