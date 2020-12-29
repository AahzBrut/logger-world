package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.dto.response.item.ItemCategoriesResponse
import io.github.loggerworld.dto.response.item.ItemEquipmentSlotsResponse
import io.github.loggerworld.dto.response.item.ItemQualitiesResponse
import io.github.loggerworld.dto.response.item.ItemStatsResponse
import io.github.loggerworld.service.ItemService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

const val CATEGORIES_URL = "/api/items/categories"
const val STATS_URL = "/api/items/stats"
const val QUALITIES_URL = "/api/items/qualities"
const val EQUIPMENT_SLOTS_URL = "/api/items/equipment-slots"

@RestController
class ItemRestController(
    private val itemService: ItemService
) {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(CATEGORIES_URL)
    fun getAllCategories(principal: Principal): ResponseObject<ItemCategoriesResponse> {
        return ResponseObject(success = true, data = itemService.getAllCategories(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(STATS_URL)
    fun getAllStats(principal: Principal): ResponseObject<ItemStatsResponse> {
        return ResponseObject(success = true, data = itemService.getAllStats(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(QUALITIES_URL)
    fun getAllQualities(principal: Principal): ResponseObject<ItemQualitiesResponse> {
        return ResponseObject(success = true, data = itemService.getAllQualities(principal.name))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(EQUIPMENT_SLOTS_URL)
    fun getAllEquipmentSlots(principal: Principal): ResponseObject<ItemEquipmentSlotsResponse> {
        return ResponseObject(success = true, data = itemService.getAllEquipmentSlots(principal.name))
    }
}