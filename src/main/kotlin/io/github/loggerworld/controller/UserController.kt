package io.github.loggerworld.controller

import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/user/sign-up")
    fun addNewUser(@RequestBody request: UserAddRequest) {
        userService.addNewUser(request)
    }
}