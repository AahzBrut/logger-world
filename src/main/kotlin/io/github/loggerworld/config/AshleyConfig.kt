package io.github.loggerworld.config

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.ecs.component.LocationComponent
import io.github.loggerworld.ecs.component.MonsterSpawnerComponent
import io.github.loggerworld.service.LocationService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AshleyConfig(
    private val entitySystems: List<EntitySystem>,
    private val locationService: LocationService,
) : LogAware {

    @Value("\${entityPoolInitialSize}")
    private var entityPoolInitialSize: Int = 0

    @Value("\${entityPoolMaxSize}")
    private var entityPoolMaxSize: Int = 0

    @Value("\${componentPoolInitialSize}")
    private var componentPoolInitialSize: Int = 0

    @Value("\${componentPoolMaxSize}")
    private var componentPoolMaxSize: Int = 0


    @Bean
    fun getEngine(): Engine {

        val engine = PooledEngine(
            entityPoolInitialSize,
            entityPoolMaxSize,
            componentPoolInitialSize,
            componentPoolMaxSize
        ).apply {
            entitySystems.forEach {
                addSystem(it)
            }
        }

        loadWorld(engine)
        logger().info("Карта мира загружена.")
        return engine
    }

    private fun loadWorld(engine: Engine) {

        locationService
            .getWorldMap()
            .locations
            .values
            .filter { it.type != LocationTypes.VOID}
            .forEach { location ->
                val locationEntity = engine.entity {
                    with<LocationComponent> {
                        locationId = location.id
                        xCoord = location.xCoord
                        yCoord = location.yCoord
                        locationType = location.type
                    }
                }
                location.monsterNests.forEach { nestData ->
                    val nestEntity = engine.entity {
                        with<MonsterSpawnerComponent> {
                            this.id = nestData.id
                            this.location = locationEntity
                            this.monsterClass = nestData.monsterClass
                            this.level = nestData.level
                            this.amount = nestData.amount
                            this.minRespawnTimer = nestData.minRespawnTime
                            this.maxRespawnTimer = nestData.maxRespawnTime
                        }
                    }
                    locationEntity[LocationComponent.mapper]!!.monsterSpawners.add(nestEntity)
                }
            }

    }

}