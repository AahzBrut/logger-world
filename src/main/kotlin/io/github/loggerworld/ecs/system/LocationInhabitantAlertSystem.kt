package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.dto.response.monster.MobNestResponse
import io.github.loggerworld.dto.response.monster.MonsterShortResponse
import io.github.loggerworld.ecs.EngineSystems.LOCATION_INHABITANT_ALERT_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.remove
import ktx.collections.GdxSet
import org.springframework.stereotype.Component

@Component
class LocationInhabitantAlertSystem(
    private val outBus: EventBus<LocationChangedEvent>
) : IteratingSystem(allOf(LocationUpdatedComponent::class).get(), LOCATION_INHABITANT_ALERT_SYSTEM.ordinal), LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val locationComp = entity[LocationComponent.mapper]!!
        outBus.dispatchEvent { event ->
            event.locationId = locationComp.locationId
            event.players = locationComp.players
                .map { playerEntity ->
                    val playerComp = playerEntity[PlayerComponent.mapper]!!
                    val moveComp = playerEntity[StateComponent.mapper]!!
                    ShortPlayerResponse(
                        playerComp.playerId,
                        playerComp.playerName,
                        playerComp.level,
                        playerComp.classId,
                        moveComp.state
                    )
                }.toMutableList()

            event.mobNests = locationComp.monsterSpawners.map { nestEntity ->
                val spawnerComponent = nestEntity[MonsterSpawnerComponent.mapper]!!
                MobNestResponse(
                    spawnerComponent.id,
                    spawnerComponent.monsterClass,
                    spawnerComponent.level,
                    spawnerComponent.amount
                )
            }.toMutableList()

            event.mobs = locationComp.spawnedMonsters.map { monsterEntity ->
                val monsterComp = monsterEntity[MonsterComponent.mapper]!!
                MonsterShortResponse(
                    monsterComp.id,
                    monsterComp.level,
                    monsterComp.monsterClass,
                    monsterComp.monsterType
                )
            }.toMutableList()
        }

        updateMoveStates(locationComp.players)

        logger().debug("\nInhabitants changed in location with id: ${locationComp.locationId}")
        entity.remove<LocationUpdatedComponent>()
    }

    private fun updateMoveStates(players: GdxSet<Entity>) {

        players.removeAll { player ->
            player[StateComponent.mapper]!!.state == States.DEPARTING
        }

        players.filter { player ->
            player[StateComponent.mapper]!!.state == States.ARRIVING
        }
            .forEach { player ->
                player[StateComponent.mapper]!!.state = States.IDLE
            }
    }
}