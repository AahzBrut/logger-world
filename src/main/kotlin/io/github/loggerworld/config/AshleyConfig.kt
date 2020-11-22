package io.github.loggerworld.config

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import io.github.loggerworld.ecs.WorldCache
import io.github.loggerworld.ecs.component.PlayerComponent
import ktx.ashley.entity
import ktx.ashley.with
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AshleyConfig(
    private val entitySystems : List<EntitySystem>,
    private val worldCache: WorldCache
) {

    @Value("\${entityPoolInitialSize}")
    private var entityPoolInitialSize: Int = 0

    @Value("\${entityPoolMaxSize}")
    private var entityPoolMaxSize: Int = 0

    @Value("\${componentPoolInitialSize}")
    private var componentPoolInitialSize: Int = 0

    @Value("\${componentPoolMaxSize}")
    private var componentPoolMaxSize: Int = 0


    @Bean
    fun getEngine() : Engine {

        val cache = worldCache.worldMap

        return PooledEngine(
            entityPoolInitialSize,
            entityPoolMaxSize,
            componentPoolInitialSize,
            componentPoolMaxSize).apply {
            entitySystems.forEach {
                addSystem(it)
            }
        }.also {
            it.entity {
                with<PlayerComponent>{}
            }
        }
    }

}