package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.dto.LocationInhabitantsChanged
import ktx.ashley.mapperFor
import ktx.collections.gdxMapOf

class LocationMapComponent : Component, Pool.Poolable {

    val locationMap = gdxMapOf<Short, LocationInhabitantsChanged>()

    override fun reset() {
        locationMap.clear()
    }

    companion object{
        val mapper = mapperFor<LocationMapComponent>()
    }
}