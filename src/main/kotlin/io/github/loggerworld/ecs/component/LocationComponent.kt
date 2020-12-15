package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.LocationTypes
import io.github.loggerworld.util.emptyEntity
import ktx.ashley.mapperFor
import ktx.collections.gdxArrayOf
import ktx.collections.gdxSetOf

class LocationComponent : Component, Pool.Poolable {

    var locationId: Short = -1
    var xCoord: Byte = -1
    var yCoord: Byte = -1
    var locationType: LocationTypes = LocationTypes.VOID
    val players = gdxSetOf<Entity>()
    val monsterSpawners = gdxSetOf<Entity>()
    val spawnedMonsters = gdxSetOf<Entity>()

    override fun reset() {
        locationId = -1
        xCoord = -1
        yCoord = -1
        locationType = LocationTypes.VOID
        players.clear()
        monsterSpawners.clear()
        spawnedMonsters.clear()
    }

    companion object {
        val mapper = mapperFor<LocationComponent>()
    }
}