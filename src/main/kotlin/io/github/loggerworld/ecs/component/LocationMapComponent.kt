package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.dto.LocationInhabitantsUpdated
import ktx.ashley.mapperFor
import ktx.collections.gdxMapOf

class LocationMapComponent : Component, Pool.Poolable {

    val locationMap = gdxMapOf<Short, LocationInhabitantsUpdated>()

    override fun reset() {
        locationMap.clear()
    }

    companion object{
        val mapper = mapperFor<LocationMapComponent>()
    }
}