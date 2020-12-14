package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.CombatComponent
import io.github.loggerworld.ecs.component.HealthComponent
import io.github.loggerworld.ecs.component.KilledComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.DealDamageToMobEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.ReceiveDamageFromMobEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.remove
import ktx.collections.isEmpty
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import kotlin.math.max

@Service
class CombatSystem(
    private val logEventBus: LogEventBus<LogEvent>
) : IteratingSystem(allOf(CombatComponent::class).get(), EngineSystems.COMBAT_SYSTEM.ordinal),
    LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combatComp = entity[CombatComponent.mapper]!!
        combatComp.attackCooldown -= deltaTime
        if (combatComp.attackCooldown > 0f) return
        combatComp.attackCooldown += combatComp.baseAttackCooldown

        if (entity.has(MonsterComponent.mapper)) {
            monsterAttack(entity)
        } else {
            playerAttack(entity)
        }
    }

    private fun monsterAttack(monster: Entity) {
        val monsterComp = monster[MonsterComponent.mapper]!!
        val locationComp = monsterComp.location[LocationComponent.mapper]!!
        val combatComp = monster[CombatComponent.mapper]!!

        if (monsterComp.target == null) {
            if (monsterComp.enemies.isEmpty()) {
                monsterComp.nest[MonsterSpawnerComponent.mapper]!!.monsterCounter++
                locationComp.spawnedMonsters.remove(monster)
                locationMap[locationComp.locationId]!!.updated = true
                monster.addComponent<RemoveComponent>(engine)
                logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} have no more targets and returns to nest.")
                return
            } else {
                monsterComp.target = monsterComp.enemies.first()
                logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} lost his target and chose another.")
                combatComp.attackCooldown = combatComp.baseAttackCooldown
                logMobAttackPlayer(monster, monsterComp.target!!)
                return
            }
        }

        val tgtHealthComp = monsterComp.target!![HealthComponent.mapper]!!
        val damage = max(0f, combatComp.damage - tgtHealthComp.defence)
        tgtHealthComp.health -= damage
        logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} hit his target for $damage damage (${tgtHealthComp.health} health left).")
        logReceiveDamageFromMobEvent(monsterComp, damage)

        if (tgtHealthComp.health <= 0f) {
            monsterComp.enemies.remove(monsterComp.target)
            monsterComp.target!!.addComponent<KilledComponent>(engine){
                this.killer = monster
                this.locationId = locationComp.locationId
            }
            monsterComp.target = null
            logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} killed his target.")
        }
    }

    private fun playerAttack(player: Entity) {
        val playerComp = player[PlayerComponent.mapper]!!
        val locationComp = playerComp.location[LocationComponent.mapper]!!
        val combatComp = player[CombatComponent.mapper]!!

        if (playerComp.target == null) {
            if (playerComp.enemies.isEmpty()) {
                player[StateComponent.mapper]!!.state = States.IDLE
                player.remove<CombatComponent>()
                return
            } else {
                playerComp.target = playerComp.enemies.first()
                logger().debug("Player ${playerComp.playerId} in location ${locationComp.locationId} lost his target and chose another.")
            }
        }

        val tgtHealthComp = playerComp.target!![HealthComponent.mapper]!!
        val damage = max(0f, combatComp.damage - tgtHealthComp.defence)
        tgtHealthComp.health -= damage
        logger().debug("Player ${playerComp.playerId} in location ${locationComp.locationId} hit his target for $damage damage (${tgtHealthComp.health} health left).")
        logDealDamageToMobEvent(playerComp, damage)

        if (tgtHealthComp.health <= 0f) {
            playerComp.enemies.remove(playerComp.target)
            playerComp.target!!.addComponent<KilledComponent>(engine){
                this.killer = player
                this.locationId = locationComp.locationId
            }
            playerComp.target = null
            logger().debug("Player ${playerComp.playerId} in location ${locationComp.locationId} killed his target.")
        }
    }

    private fun logReceiveDamageFromMobEvent(monsterComp: MonsterComponent, damage: Float) {
        val playerComp = monsterComp.target!![PlayerComponent.mapper]!!
        val event = logEventBus.newEvent(ReceiveDamageFromMobEvent::class) as ReceiveDamageFromMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.damage = damage.toInt()
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun logMobAttackPlayer(mob: Entity, player: Entity) {
        val monsterComp = mob[MonsterComponent.mapper]!!
        val playerComp = player[PlayerComponent.mapper]!!
        val attackEvent = logEventBus.newEvent(AttackedByMobEvent::class) as AttackedByMobEvent
        attackEvent.playerId = playerComp.playerId
        attackEvent.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        attackEvent.created = OffsetDateTime.now()
        logEventBus.pushEvent(attackEvent)
    }

    private fun logDealDamageToMobEvent(playerComp: PlayerComponent, damage: Float) {
        val monsterComp = playerComp.target!![MonsterComponent.mapper]!!
        val event = logEventBus.newEvent(DealDamageToMobEvent::class) as DealDamageToMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.damage = damage.toInt()
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }
}
