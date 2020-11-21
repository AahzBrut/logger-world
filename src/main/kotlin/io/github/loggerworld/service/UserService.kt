package io.github.loggerworld.service

import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.service.domain.UserDomainService
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDomainService: UserDomainService,
) {

    fun addNewUser(request: UserAddRequest) {

        userDomainService.addNewUser(request)
    }

    fun authenticate(request: UserLoginRequest) : Boolean {

        return userDomainService.authenticate(request)
    }
}