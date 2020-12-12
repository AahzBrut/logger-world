package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.dto.response.monster.MobNestResponse
import io.github.loggerworld.dto.response.monster.MonsterShortResponse
import io.github.loggerworld.ecs.EngineSystems.LOCATION_INHABITANT_ALERT_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.collections.GdxSet
import org.springframework.stereotype.Component

@Component
class LocationInhabitantAlertSystem(
    private val outBus: EventBus<LocationChangedEvent>
) : EntitySystem(LOCATION_INHABITANT_ALERT_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun update(deltaTime: Float) {
        locationMap
            .forEach {
                if (it.value.updated) {

                    val locationComp = locationMap[it.key].entity[LocationComponent.mapper]!!

                    outBus.dispatchEvent { event ->
                        event.locationId = it.key
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

                    logger().debug("\nInhabitants changed in location with id: ${it.key}")
                    it.value.updated = false
                }
            }
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