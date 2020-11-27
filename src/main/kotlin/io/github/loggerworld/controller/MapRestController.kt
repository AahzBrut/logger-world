package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.ecs.WorldCache
import io.github.loggerworld.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

const val LOCATION_TYPES_URL = "/api/location-types"
const val LOCATIONS_URL = "/api/locations"

@RestController
class MapRestController(
    private val userService: UserService,
    private val worldCache: WorldCache,
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(LOCATION_TYPES_URL)
    fun getAllLocationTypes(principal: Principal) : LocationTypesResponse {
        return worldCache.getLocationTypes(userService.getUserLanguage(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(LOCATIONS_URL)
    fun getAllLocations(principal: Principal) : LocationsResponse {
        return worldCache.getLocations(userService.getUserLanguage(principal.name))
    }
}