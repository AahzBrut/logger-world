package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.KilledComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationMapComponent
import io.github.loggerworld.ecs.component.MonsterComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.RemoveComponent
import io.github.loggerworld.messagebus.LogEventBus
import io.github.loggerworld.messagebus.event.LogEvent
import io.github.loggerworld.messagebus.event.PlayerKillMobEvent
import io.github.loggerworld.messagebus.event.PlayerKilledByMobEvent
import io.github.loggerworld.util.LogAware
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.remove
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GraveyardSystem(
    private val logEventBus: LogEventBus<LogEvent>
) : IteratingSystem(allOf(KilledComponent::class).get(), EngineSystems.GRAVEYARD_SYSTEM.ordinal),
    LogAware {

    private val locationMap by lazy { engine.getEntitiesFor(allOf(LocationMapComponent::class).get())[0][LocationMapComponent.mapper]!!.locationMap }

    override fun processEntity(deceased: Entity, deltaTime: Float) {
        val killedComp = deceased[KilledComponent.mapper]!!
        val locationComp = locationMap[killedComp.locationId].entity[LocationComponent.mapper]!!
        locationMap[killedComp.locationId].updated = true

        if (deceased.has(PlayerComponent.mapper)){
            val playerComp = deceased[PlayerComponent.mapper]!!
            val monsterComp = killedComp.killer[MonsterComponent.mapper]!!
            logPlayerKilledEvent(playerComp, monsterComp)
            locationComp.players.remove(deceased)
        } else {
            val playerComp = killedComp.killer[PlayerComponent.mapper]!!
            val monsterComp = deceased[MonsterComponent.mapper]!!
            logMonsterKilledEvent(playerComp, monsterComp)
            locationComp.spawnedMonsters.remove(deceased)
        }
        deceased.remove<KilledComponent>()
        deceased.addComponent<RemoveComponent>(engine)
    }

    private fun logPlayerKilledEvent(playerComp: PlayerComponent, monsterComp: MonsterComponent) {
        val event = logEventBus.newEvent(PlayerKilledByMobEvent::class) as PlayerKilledByMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }

    private fun logMonsterKilledEvent(playerComp: PlayerComponent, monsterComp: MonsterComponent) {
        val event = logEventBus.newEvent(PlayerKillMobEvent::class) as PlayerKillMobEvent
        event.playerId = playerComp.playerId
        event.monsterName = "${monsterComp.monsterClass}(${monsterComp.monsterType}) ${monsterComp.level} Lvl"
        event.created = OffsetDateTime.now()
        logEventBus.pushEvent(event)
    }
}
