package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.CombatComponent
import io.github.loggerworld.ecs.component.HealthComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.remove
import ktx.collections.isEmpty
import org.springframework.stereotype.Service

@Service
class CombatSystem
    : IteratingSystem(allOf(CombatComponent::class).get(), EngineSystems.COMBAT_SYSTEM.ordinal),
    LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val combatComp = entity[CombatComponent.mapper]!!
        combatComp.attackCooldown -= deltaTime
        if (combatComp.attackCooldown > 0f) return
        combatComp.attackCooldown += combatComp.baseAttackCooldown

        if (entity.has(PlayerComponent.mapper)) {
            playerAttack(entity)
        } else {
            monsterAttack(entity)
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
            }
        }

        val tgtHealthComp = monsterComp.target!![HealthComponent.mapper]!!
        val damage = combatComp.damage - tgtHealthComp.defence
        tgtHealthComp.health -= damage
        logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} hit his target for $damage damage (${tgtHealthComp.health} health left).")

        if (tgtHealthComp.health <= 0f) {
            monsterComp.enemies.remove(monsterComp.target)
            monsterComp.target!!.addComponent<RemoveComponent>(engine)
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
        val damage = combatComp.damage - tgtHealthComp.defence
        tgtHealthComp.health -= damage
        logger().debug("Player ${playerComp.playerId} in location ${locationComp.locationId} hit his target for $damage damage (${tgtHealthComp.health} health left).")

        if (tgtHealthComp.health <= 0f) {
            val target = playerComp.target!!
            playerComp.enemies.remove(playerComp.target)
            playerComp.target!!.addComponent<RemoveComponent>(engine)
            playerComp.target = null
            logger().debug("Player ${playerComp.playerId} in location ${locationComp.locationId} killed his target.")

            val monsterComp = target[MonsterComponent.mapper]!!
            logger().debug("Mob ${monsterComp.monsterType} ${monsterComp.monsterClass} ${monsterComp.level}lvl in location ${locationComp.locationId} was killed.")
        }
    }
}