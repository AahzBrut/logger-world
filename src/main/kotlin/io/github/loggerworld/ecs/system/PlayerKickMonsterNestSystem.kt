package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.CombatComponent
import io.github.loggerworld.ecs.component.HealthComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.AttackMobEvent
import io.github.loggerworld.messagebus.event.AttackedByMobEvent
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.NestKickEvent
import io.github.loggerworld.messagebus.event.PlayerKickMonsterNestCommand
import io.github.loggerworld.service.MonsterService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.hasNot
import ktx.ashley.with
import ktx.collections.GdxSet
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class PlayerKickMonsterNestSystem(
    private val kickCommandEventBus: EventBus<PlayerKickMonsterNestCommand>,
    private val monsterService: MonsterService,
    private val logEventBus: LogEventBus<LogEvent>
) :
    EntitySystem(EngineSystems.PLAYER_KICK_MONSTER_NEST_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {

        while (kickCommandEventBus.receiveEvent { kickCommand ->
                val playerEntity = playerMap[kickCommand.playerId]
                val location = playerEntity[PlayerComponent.mapper]!!.location
                val locationComp = location[LocationComponent.mapper]!!
                val spawners = locationComp.monsterSpawners
                val spawner = getSpawner(spawners, kickCommand.monsterNestId)

                if (spawner.amount-- > 0) {
                    val monster = spawnMonster(spawner, playerEntity)
                    locationComp.spawnedMonsters.add(monster)
                    if (location.hasNot(LocationUpdatedComponent.mapper))
                        location.addComponent<LocationUpdatedComponent>(engine)
                    logKickNestEvent(kickCommand)
                    logMobAttackPlayer(monster, playerEntity)
                    attackMonster(playerEntity, monster)
                }
            }) {
            // Empty body
        }
    }

    private fun attackMonster(player: Entity, monster: Entity) {
        val playerComp = player[PlayerComponent.mapper]!!
        if (player.hasNot(CombatComponent.mapper)) {
            player.addComponent<CombatComponent>(engine) {
                this.baseAttackCooldown = 1f / (1f + .01f * playerComp.stats[PlayerStatEnum.SPD]!!)
                this.attackCooldown = baseAttackCooldown
                this.damage = playerComp.stats[PlayerStatEnum.ATK]!!
                this.enemies.add(monster)
                this.target = monster
                this.locationId = playerComp.location[LocationComponent.mapper]!!.locationId
                logPlayerAttackMob(player, monster)
            }
        } else {
            player[CombatComponent.mapper]!!.enemies.add(monster)
        }
        logger().debug("Player ${playerComp.playerId} attacks monster ${monster[MonsterComponent.mapper]!!.id}")
    }

    private fun logPlayerAttackMob(player: Entity, monster: Entity) {
        val monsterComp = monster[MonsterComponent.mapper]!!
        val playerComp = player[PlayerComponent.mapper]!!
        val attackEvent = logEventBus.newEvent(AttackMobEvent::class) as AttackMobEvent
        attackEvent.playerId = playerComp.playerId
        attackEvent.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        attackEvent.created = OffsetDateTime.now()
        logEventBus.pushEvent(attackEvent)
    }

    private fun logKickNestEvent(command: PlayerKickMonsterNestCommand) {
        val kickEvent = logEventBus.newEvent(NestKickEvent::class) as NestKickEvent
        kickEvent.playerId = command.playerId
        kickEvent.nestId = command.monsterNestId
        kickEvent.created = OffsetDateTime.now()
        logEventBus.pushEvent(kickEvent)
    }

    private fun getSpawner(spawners: GdxSet<Entity>, spawnerId: Short): MonsterSpawnerComponent {

        return spawners
            .map {
                it[MonsterSpawnerComponent.mapper]!!
            }
            .first {
                it.id == spawnerId
            }
    }

    private fun spawnMonster(spawnerComp: MonsterSpawnerComponent, playerEntity: Entity): Entity {
        val monsterSpawnerData = monsterService.getMonsterSpawnerData()
        val type = monsterSpawnerData
            .classes[spawnerComp.monsterClass]!!
            .levels[spawnerComp.level]!!
            .getRandomMonsterType()
        val stats = monsterSpawnerData
            .classes[spawnerComp.monsterClass]!!
            .levels[spawnerComp.level]!!
            .types[type]!!
            .stats

        logger().debug("Monster ${spawnerComp.monsterCounter + 1} attacks player ${playerEntity[PlayerComponent.mapper]!!.playerId}")

        val mobId = ++spawnerComp.monsterCounter

        return engine.entity {
            with<MonsterComponent> {
                id = mobId
                level = spawnerComp.level
                monsterClass = spawnerComp.monsterClass
                monsterType = type
                health = stats[PlayerStatEnum.HP]!!
                attack = stats[PlayerStatEnum.ATK]!!
                defence = stats[PlayerStatEnum.DEF]!!
                state = States.IN_COMBAT
                location = spawnerComp.location
            }
            with<CombatComponent> {
                baseAttackCooldown = 1.0f
                attackCooldown = baseAttackCooldown
                damage = stats[PlayerStatEnum.ATK]!!.toFloat()
                enemies.add(playerEntity)
                target = playerEntity
                locationId = spawnerComp.location[LocationComponent.mapper]!!.locationId
            }
            with<HealthComponent> {
                health = stats[PlayerStatEnum.HP]!!.toFloat()
                defence = stats[PlayerStatEnum.DEF]!!.toFloat()
            }
        }
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
}