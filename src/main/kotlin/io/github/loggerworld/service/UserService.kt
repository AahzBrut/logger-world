package io.github.loggerworld.service

import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.exception.UserAlreadyExistsException
import io.github.loggerworld.mapper.Mapper
import io.github.loggerworld.repository.user_account.UserAccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
    private val userAccountRepository: UserAccountRepository,
    private val userMapper: Mapper<UserAccount, UserAddRequest>,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun addNewUser(request: UserAddRequest) {

        if (userAccountRepository.existsByLoginName(request.userName)) throw UserAlreadyExistsException("User with login name: ${request.userName} already exists.")

        val newUserAccount = userMapper.from(request)
        newUserAccount.password = passwordEncoder.encode(request.password)
        userAccountRepository.save(newUserAccount)
    }
}