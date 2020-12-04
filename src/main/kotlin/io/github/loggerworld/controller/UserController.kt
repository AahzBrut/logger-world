package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


const val SIGN_UP_URL = "/api/user/sign-up"

@RestController
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(SIGN_UP_URL)
    fun addNewUser(@RequestBody request: UserAddRequest): ResponseObject<Any> {

        userService.addNewUser(request)
        return ResponseObject(true, "User ${request.userName} successfully registered.")
    }
}