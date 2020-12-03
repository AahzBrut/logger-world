package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.github.loggerworld.ecs.EngineSystems.MONSTER_RESPAWN_SYSTEM
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.util.LogAware
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Component

@Component
class MonsterRespawnSystem
    : IteratingSystem(allOf(MonsterSpawnerComponent::class).get(), MONSTER_RESPAWN_SYSTEM.ordinal),
    LogAware {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val monsterSpawnerComponent = entity[MonsterSpawnerComponent.mapper]!!

    }
}