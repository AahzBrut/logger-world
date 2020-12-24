package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.CombatComponent
import io.github.loggerworld.ecs.component.HealthComponent
import io.github.loggerworld.ecs.component.KilledComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.AttackMobEvent
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
import ktx.collections.isEmpty
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import kotlin.math.max

@Service
class CombatSystem(
    private val logEventBus: LogEventBus<LogEvent>
) : IteratingSystem(allOf(CombatComponent::class).get(), EngineSystems.COMBAT_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combatComp = entity[CombatComponent.mapper]!!
        combatComp.attackCooldown -= deltaTime
        if (combatComp.attackCooldown > 0f) return
        combatComp.attackCooldown += combatComp.baseAttackCooldown
        attack(entity, combatComp)
    }

    private fun attack(entity: Entity, combatComp: CombatComponent) {
        if (combatComp.target == null) {
            if (combatComp.enemies.isEmpty()) {
                despawnMobWithoutEnemies(entity)
                return
            } else {
                combatComp.target = combatComp.enemies.first()
                combatComp.attackCooldown = combatComp.baseAttackCooldown
                logAttackEvent(entity, combatComp.target!!)
                return
            }
        }
        val tgtHealthComp = combatComp.target!![HealthComponent.mapper]!!
        val damage = max(0f, combatComp.damage - tgtHealthComp.defence)
        tgtHealthComp.health -= damage
        logDamageEvent(entity, combatComp.target!!, damage)

        if (tgtHealthComp.health <= 0f) {
            combatComp.target!!.addComponent<KilledComponent>(engine){
                this.killer = entity
                this.locationId = combatComp.locationId
            }
            combatComp.enemies.remove(combatComp.target)
            combatComp.target = null
        }
    }

    private fun logDamageEvent(entity: Entity, target: Entity, damage: Float) {
        if (entity.has(MonsterComponent.mapper)) {
            val monsterComp = entity[MonsterComponent.mapper]!!
            val playerComp = target[PlayerComponent.mapper]!!
            val event = logEventBus.newEvent(ReceiveDamageFromMobEvent::class) as ReceiveDamageFromMobEvent
            event.playerId = playerComp.playerId
            event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
            event.damage = damage.toInt()
            event.created = OffsetDateTime.now()
            logEventBus.pushEvent(event)
        } else {
            val playerComp = entity[PlayerComponent.mapper]!!
            val monsterComp = target[MonsterComponent.mapper]!!
            val event = logEventBus.newEvent(DealDamageToMobEvent::class) as DealDamageToMobEvent
            event.playerId = playerComp.playerId
            event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
            event.damage = damage.toInt()
            event.created = OffsetDateTime.now()
            logEventBus.pushEvent(event)
        }
    }

    private fun logAttackEvent(entity: Entity, target: Entity) {
        if (entity.has(MonsterComponent.mapper)) {
            val monsterComp = entity[MonsterComponent.mapper]!!
            val playerComp = target[PlayerComponent.mapper]!!
            val attackEvent = logEventBus.newEvent(AttackedByMobEvent::class) as AttackedByMobEvent
            attackEvent.playerId = playerComp.playerId
            attackEvent.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
            attackEvent.created = OffsetDateTime.now()
            logEventBus.pushEvent(attackEvent)
        } else {
            val monsterComp = target[MonsterComponent.mapper]!!
            val playerComp = entity[PlayerComponent.mapper]!!
            val attackEvent = logEventBus.newEvent(AttackMobEvent::class) as AttackMobEvent
            attackEvent.playerId = playerComp.playerId
            attackEvent.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
            attackEvent.created = OffsetDateTime.now()
            logEventBus.pushEvent(attackEvent)
        }
    }

    private fun despawnMobWithoutEnemies(entity: Entity) {
        val monsterComp = entity[MonsterComponent.mapper] ?: return
        val locationComp = monsterComp.location[LocationComponent.mapper]!!
        monsterComp.nest[MonsterSpawnerComponent.mapper]!!.monsterCounter++
        locationComp.spawnedMonsters.remove(entity)
        monsterComp.location.addComponent<LocationUpdatedComponent>(engine)
        entity.addComponent<RemoveComponent>(engine)
        logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} have no more targets and returns to nest.")
        return
    }
}
