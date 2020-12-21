package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.KilledComponent
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.LocationUpdatedComponent
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
import ktx.ashley.hasNot
import ktx.ashley.remove
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class GraveyardSystem(
    private val logEventBus: LogEventBus<LogEvent>
) : IteratingSystem(allOf(KilledComponent::class).get(), EngineSystems.GRAVEYARD_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(deceased: Entity, deltaTime: Float) {
        val killedComp = deceased[KilledComponent.mapper]!!

        if (deceased.has(PlayerComponent.mapper)){
            val playerComp = deceased[PlayerComponent.mapper]!!
            val monsterComp = killedComp.killer[MonsterComponent.mapper]!!
            logPlayerKilledEvent(playerComp, monsterComp)
            playerComp.location[LocationComponent.mapper]!!.players.remove(deceased)
            if (playerComp.location.hasNot(LocationUpdatedComponent.mapper)) playerComp.location.addComponent<LocationUpdatedComponent>(engine)
        } else {
            val playerComp = killedComp.killer[PlayerComponent.mapper]!!
            val monsterComp = deceased[MonsterComponent.mapper]!!
            logMonsterKilledEvent(playerComp, monsterComp)
            playerComp.location[LocationComponent.mapper]!!.spawnedMonsters.remove(deceased)
            if (playerComp.location.hasNot(LocationUpdatedComponent.mapper)) playerComp.location.addComponent<LocationUpdatedComponent>(engine)
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
