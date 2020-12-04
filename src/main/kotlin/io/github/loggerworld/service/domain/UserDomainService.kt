package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.UserPropertyTypes
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.dto.response.user.UserResponse
import io.github.loggerworld.exception.EmailAlreadyExistsException
import io.github.loggerworld.exception.UserAlreadyExistsException
import io.github.loggerworld.mapper.user_account.UserMapper
import io.github.loggerworld.mapper.user_account.UserResponseMapper
import io.github.loggerworld.repository.user_account.UserAccountRepository
import io.github.loggerworld.repository.user_account.UserPropertyRepository
import io.github.loggerworld.util.LogAware
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserDomainService(
    private val userAccountRepository: UserAccountRepository,
    private val userMapper: UserMapper,
    private val userResponseMapper: UserResponseMapper,
    private val passwordEncoder: PasswordEncoder,
    private val userPropertyRepository: UserPropertyRepository,
) : LogAware {

    @Transactional
    fun addNewUser(request: UserAddRequest) {

        request.email = request.email.trim().toLowerCase()

        if (userAccountRepository.existsByLoginName(request.userName)) throw UserAlreadyExistsException("User with login name: ${request.userName} already exists.")
        if (userPropertyRepository.existsByValueAndPropertyTypeId(request.email, UserPropertyTypes.EMAIL.ordinal.toShort())) throw EmailAlreadyExistsException("User with email: ${request.email} already exists.")

        val newUserAccount = userMapper.from(request)
        newUserAccount.password = passwordEncoder.encode(request.password)
        userAccountRepository.save(newUserAccount)
    }

    @Transactional
    fun authenticate(request: UserLoginRequest) : Boolean {

        val user = userAccountRepository.findByLoginName(request.userName) ?: return false
        return passwordEncoder.matches(request.password, user.password)
    }

    @Transactional
    fun getUserByName(userName: String) : UserResponse? {

        val user = userAccountRepository.findByLoginName(userName)
        user ?: return null

        return userResponseMapper.from(user)
    }

    fun getUserById(userId: Long): UserResponse {

        return userResponseMapper.from(userAccountRepository.getOne(userId))
    }
}