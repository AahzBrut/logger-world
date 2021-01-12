package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.domain.enums.CombatEventTypes.ATTACKED_BY_MOB
import io.github.loggerworld.domain.enums.CombatEventTypes.ATTACK_MOB
import io.github.loggerworld.domain.enums.CombatEventTypes.DEAL_DAMAGE_MOB
import io.github.loggerworld.domain.enums.CombatEventTypes.RECEIVE_DAMAGE_MOB
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
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.AttackMobEvent
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.CombatEvent
import io.github.loggerworld.messagebus.event.LogEvent
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
    private val logEventBus: LogEventBus<LogEvent>,
    private val combatEventBus: EventBus<CombatEvent>,
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
        val selfHealthComp = entity[HealthComponent.mapper]!!
        val targetCombatComp = combatComp.target!![CombatComponent.mapper]!!
        val damage = max(0f, combatComp.damage - tgtHealthComp.defence)
        tgtHealthComp.health -= damage
        targetCombatComp.damageCounters.computeIfAbsent(entity) { 0f }
        targetCombatComp.damageCounters[entity] = targetCombatComp.damageCounters[entity]!! + damage
        val playerId = if (entity.has(PlayerComponent.mapper)) entity[PlayerComponent.mapper]!!.playerId else combatComp.target!![PlayerComponent.mapper]!!.playerId
        val monsterId = if (entity.has(MonsterComponent.mapper)) entity[MonsterComponent.mapper]!!.id else combatComp.target!![MonsterComponent.mapper]!!.id
        combatEventBus.dispatchEvent { combatEvent ->
            combatEvent.playerId = playerId
            combatEvent.eventType = if (entity.has(PlayerComponent.mapper)) DEAL_DAMAGE_MOB else RECEIVE_DAMAGE_MOB
            combatEvent.enemyId = monsterId
            combatEvent.damage = damage
            combatEvent.enemyHealth = if (entity.has(PlayerComponent.mapper)) tgtHealthComp.health else selfHealthComp.health
            combatEvent.playerHealth = if (entity.has(PlayerComponent.mapper)) selfHealthComp.health else tgtHealthComp.health
        }

        if (tgtHealthComp.health <= 0f) {
            combatComp.target!!.addComponent<KilledComponent>(engine) {
                this.killer = entity
                this.locationId = combatComp.locationId
            }
            combatComp.enemies.remove(combatComp.target)
            combatComp.target = null
        }
    }

    private fun logAttackEvent(entity: Entity, target: Entity) {
        if (entity.has(MonsterComponent.mapper)) {
            val monsterComp = entity[MonsterComponent.mapper]!!
            val playerComp = target[PlayerComponent.mapper]!!
            val monsterHealthComp = entity[HealthComponent.mapper]!!
            val playerHealthComp = target[HealthComponent.mapper]!!
            combatEventBus.dispatchEvent { combatEvent ->
                combatEvent.playerId = playerComp.playerId
                combatEvent.eventType = ATTACKED_BY_MOB
                combatEvent.enemyId = monsterComp.id
                combatEvent.damage = 0f
                combatEvent.playerHealth = playerHealthComp.health
                combatEvent.enemyHealth = monsterHealthComp.health
            }
            val attackEvent = logEventBus.newEvent(AttackedByMobEvent::class) as AttackedByMobEvent
            attackEvent.playerId = playerComp.playerId
            attackEvent.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
            attackEvent.created = OffsetDateTime.now()
            logEventBus.pushEvent(attackEvent)
        } else {
            val monsterComp = target[MonsterComponent.mapper]!!
            val playerComp = entity[PlayerComponent.mapper]!!
            val monsterHealthComp = target[HealthComponent.mapper]!!
            val playerHealthComp = entity[HealthComponent.mapper]!!
            combatEventBus.dispatchEvent { combatEvent ->
                combatEvent.playerId = playerComp.playerId
                combatEvent.eventType = ATTACK_MOB
                combatEvent.enemyId = monsterComp.id
                combatEvent.damage = 0f
                combatEvent.playerHealth = playerHealthComp.health
                combatEvent.enemyHealth = monsterHealthComp.health
            }
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
