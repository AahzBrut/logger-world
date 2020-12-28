package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.commands.PlayerEquipItemRequest
import io.github.loggerworld.dto.request.commands.PlayerKickMonsterNestRequest
import io.github.loggerworld.dto.request.commands.PlayerMoveRequest
import io.github.loggerworld.dto.request.commands.PlayerStartGameRequest
import io.github.loggerworld.service.PlayerService
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.WS_PLAYERS_EQUIP_ITEM
import io.github.loggerworld.util.WS_PLAYERS_KICK_NEST
import io.github.loggerworld.util.WS_PLAYERS_MOVE
import io.github.loggerworld.util.WS_PLAYERS_REQUEST_EQUIPMENT
import io.github.loggerworld.util.WS_PLAYERS_REQUEST_INVENTORY
import io.github.loggerworld.util.WS_PLAYERS_START
import io.github.loggerworld.util.logger
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class PlayerController(
    private val playerService: PlayerService,
) : LogAware {

    @MessageMapping(WS_PLAYERS_START)
    fun startGameForPlayer(principal: Principal, request: PlayerStartGameRequest) {
        logger().debug("User: ${principal.name} IN: startGameForPlayer()")
        playerService.startGameForPlayer(principal.name, request)
        logger().debug("User: ${principal.name} OUT: startGameForPlayer()")
    }

    @MessageMapping(WS_PLAYERS_MOVE)
    fun movePlayer(principal: Principal, request: PlayerMoveRequest) {
        playerService.movePlayer(principal.name, request)
    }

    @MessageMapping(WS_PLAYERS_KICK_NEST)
    fun kickMonsterNest(principal: Principal, request: PlayerKickMonsterNestRequest) {
        playerService.kickMonsterNest(principal.name, request)
    }

    @MessageMapping(WS_PLAYERS_EQUIP_ITEM)
    fun equipItem(principal: Principal, request: PlayerEquipItemRequest) {
        playerService.equipItem(principal.name, request)
    }

    @MessageMapping(WS_PLAYERS_REQUEST_INVENTORY)
    fun requestInventory(principal: Principal) {
        playerService.requestInventory(principal.name)
    }

    @MessageMapping(WS_PLAYERS_REQUEST_EQUIPMENT)
    fun requestEquipment(principal: Principal) {
        playerService.requestEquipment(principal.name)
    }
}