package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.ecs.EngineSystems.LOCATION_INHABITANT_ALERT_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MoveStateComponent
import io.github.loggerworld.ecs.component.MoveStates
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.messagebus.NotificationEventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.collections.GdxArray
import org.springframework.stereotype.Component

@Component
class LocationInhabitantAlertSystem(
    private val outBus: NotificationEventBus<LocationChangedEvent>
) : EntitySystem(LOCATION_INHABITANT_ALERT_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun update(deltaTime: Float) {
        locationMap
            .forEach {
                if (it.value.updated) {

                    val players = locationMap[it.key].entity[LocationComponent.mapper]!!.players

                    val event = outBus.newEvent().also { event ->
                        event.locationId = it.key
                        event.players = players
                            .map { playerEntity ->
                                val playerComp = playerEntity[PlayerComponent.mapper]!!
                                val moveComp = playerEntity[MoveStateComponent.mapper]!!
                                ShortPlayerResponse(
                                    playerComp.playerId,
                                    playerComp.playerName,
                                    playerComp.level,
                                    playerComp.classId,
                                    moveComp.state
                                )
                            }.toMutableList()
                        event.mobs = mutableListOf()
                    }
                    outBus.pushEvent(event)

                    updateMoveStates(players)

                    logger().debug("Inhabitants changed in location with id: ${it.key}")
                    it.value.updated = false
                }
            }
    }

    private fun updateMoveStates(players: GdxArray<Entity>) {

        players.removeAll {player ->
            player[MoveStateComponent.mapper]!!.state == MoveStates.DEPARTING
        }

        players.filter {player ->
            player[MoveStateComponent.mapper]!!.state == MoveStates.ARRIVING
        }
            .forEach { player ->
                player[MoveStateComponent.mapper]!!.state = MoveStates.STATIONARY
            }
    }
}