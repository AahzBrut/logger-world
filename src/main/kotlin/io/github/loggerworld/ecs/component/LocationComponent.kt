package io.github.loggerworld.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import io.github.loggerworld.domain.enums.LocationTypes
import ktx.ashley.mapperFor

class LocationComponent : Component, Pool.Poolable{

    var locationId: Short = -1
    var xCoord: Byte = -1
    var yCoord: Byte = -1
    var locationType: LocationTypes = LocationTypes.VOID

    override fun reset() {
        locationId = -1
        xCoord = -1
        yCoord = -1
        locationType = LocationTypes.VOID
    }

    companion object {
        val mapper = mapperFor<LocationComponent>()
    }
}