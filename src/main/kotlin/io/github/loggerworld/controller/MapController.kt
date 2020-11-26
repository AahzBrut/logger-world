package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.geography.LocationTypesResponse
import io.github.loggerworld.dto.response.geography.LocationsResponse
import io.github.loggerworld.ecs.WorldCache
import io.github.loggerworld.service.UserService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_DS_MAP
import io.github.loggerworld.util.WS_DS_MAP_LOCATION_TYPES
import io.github.loggerworld.util.WS_DS_PLAYER_MESSAGES
import io.github.loggerworld.util.WS_MAP
import io.github.loggerworld.util.WS_MAP_LOCATION_TYPES
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class MapController(
    private val userService: UserService,
    private val worldCache: WorldCache,
) : LogAware {

    @MessageMapping(WS_MAP_LOCATION_TYPES)
    @SendToUser(WS_DS_MAP_LOCATION_TYPES)
    fun getLocationTypes(principal: Principal) : LocationTypesResponse {
        return worldCache.getLocationTypes(userService.getUserLanguage(principal.name))
    }

    @MessageMapping(WS_MAP)
    @SendToUser(WS_DS_MAP)
    fun getLocations(principal: Principal) : LocationsResponse {
        return worldCache.getLocations(userService.getUserLanguage(principal.name))
    }
}