package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.ecs.EngineSystems.PLAYER_SPAWN_SYSTEM
import io.github.loggerworld.ecs.component.EquipmentComponent
import io.github.loggerworld.ecs.component.HealthComponent
import io.github.loggerworld.ecs.component.InventoryComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.ecs.component.StateComponent
import io.github.loggerworld.ecs.component.States
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.LoginEvent
import io.github.loggerworld.messagebus.event.PlayerStartCommand
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.hasNot
import ktx.ashley.with
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class PlayerSpawnSystem(
    private val startEventBus: EventBus<PlayerStartCommand>,
    private val logEventBus: LogEventBus<LogEvent>,
    private val playerService: PlayerService,
) : EntitySystem(PLAYER_SPAWN_SYSTEM.ordinal), LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }
    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {
        while (startEventBus.receiveEvent { event ->
                if (!playerMap.containsKey(event.playerId)) {
                    val player = spawnPlayer(event)

                    logger().debug("\nPlayer with id:${event.playerId} spawned into location with id:${event.locationId}")

                    addPlayerToTargetLocation(player, event)
                } else {

                    val playerComp = playerMap[event.playerId][PlayerComponent.mapper]!!
                    val locationComp = playerComp.location[LocationComponent.mapper]!!
                    event.locationId = locationComp.locationId
                    logger().debug("\nPlayer with id:${event.playerId} connected back into location with id:${event.locationId}")

                }
                if (locationMap[event.locationId].entity.hasNot(LocationUpdatedComponent.mapper))
                    locationMap[event.locationId].entity.addComponent<LocationUpdatedComponent>(engine)
                logLoginEvent(event)
            }) {
            // Empty body
        }
    }

    private fun logLoginEvent(event: PlayerStartCommand) {
        val loginEvent = logEventBus.newEvent(LoginEvent::class) as LoginEvent
        loginEvent.playerId = event.playerId
        loginEvent.locationId = event.locationId
        loginEvent.created = OffsetDateTime.now()
        logEventBus.pushEvent(loginEvent)
    }

    private fun addPlayerToTargetLocation(player: Entity, command: PlayerStartCommand) {
        val locationEntity = locationMap[command.locationId].entity
        val locationComponent = locationEntity[LocationComponent.mapper]!!
        locationComponent.players.add(player)
    }

    private fun spawnPlayer(command: PlayerStartCommand): Entity {

        val playerData = playerService.getPlayerById(command.playerId)
        val hp = playerData.effectiveStats[PlayerStatEnum.HP.ordinal.toByte()]!!
        val def = playerData.effectiveStats[PlayerStatEnum.DEF.ordinal.toByte()]!!

        return engine.entity {
            with<PlayerComponent> {
                playerId = command.playerId
                userId = command.userId
                playerName = command.name
                classId = command.classId
                level = command.level
                stats = playerData.effectiveStats.map {
                    PlayerStatEnum.getById(it.key) to it.value
                }.toMap()
                location = locationMap[command.locationId].entity
            }
            with<StateComponent> { state = States.ARRIVING }
            with<HealthComponent> {
                health = hp
                defence = def
            }
            with<InventoryComponent> {}
            with<EquipmentComponent> {}
        }
    }
}