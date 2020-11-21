package io.github.loggerworld.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


const val TEST_URL = "/api/test"

@RestController
class TestController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(TEST_URL)
    fun securityTest(principal: Principal) : String {
        return principal.name
    }

}