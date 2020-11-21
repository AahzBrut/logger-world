package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.dto.response.UserResponse
import io.github.loggerworld.exception.UserAlreadyExistsException
import io.github.loggerworld.mapper.Mapper
import io.github.loggerworld.repository.user_account.UserAccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserDomainService(
    private val userAccountRepository: UserAccountRepository,
    private val userMapper: Mapper<UserAccount, UserAddRequest>,
    private val userResponseMapper: Mapper<UserResponse, UserAccount>,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun addNewUser(request: UserAddRequest) {

        if (userAccountRepository.existsByLoginName(request.userName)) throw UserAlreadyExistsException("User with login name: ${request.userName} already exists.")

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
}