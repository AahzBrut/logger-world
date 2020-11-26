package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.dto.response.character.ShortPlayerResponse
import io.github.loggerworld.ecs.EngineSystems.LOCATION_INHABITANT_ALERT_SYSTEM
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.messagebus.NotificationEventBus
import io.github.loggerworld.messagebus.event.LocationChangedEvent
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Component

@Component
class LocationInhabitantAlertSystem(
    private val outBus: NotificationEventBus<LocationChangedEvent>
) : EntitySystem(LOCATION_INHABITANT_ALERT_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }
    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {
        locationMap
            .forEach {
                if (it.value.updated) {

                    val players = locationMap[it.key].entity[LocationComponent.mapper]!!.players

                    val event = outBus.newEvent().also {event ->
                        event.locationId = it.key
                        event.players = players.map { playerId ->
                            val playerComp = playerMap[playerId][PlayerComponent.mapper]!!
                            ShortPlayerResponse(playerId, playerComp.playerName, playerComp.level, playerComp.classId)
                        }.toMutableList()
                        event.mobs = mutableListOf()
                    }
                    outBus.pushEvent(event)

                    logger().debug("Inhabitants changed in location with id: ${it.key}")
                    it.value.updated = false
                }
            }
    }
}