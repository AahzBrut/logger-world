package io.github.loggerworld.mapper.user_account

import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.dto.response.UserResponse
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class UserResponseMapper : Mapper<UserResponse, UserAccount> {

    override fun from(source: UserAccount): UserResponse {

        return UserResponse(
            source.id!!,
            source.loginName,
            Languages.getById(source.language!!.id!!),
            source.displayName,
        )

    }
}