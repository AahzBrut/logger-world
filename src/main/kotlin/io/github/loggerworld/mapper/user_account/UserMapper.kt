package io.github.loggerworld.mapper.user_account

import io.github.loggerworld.domain.enums.UserPropertyTypes
import io.github.loggerworld.domain.enums.UserStatuses
import io.github.loggerworld.domain.main.Language
import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.domain.user_account.UserProperty
import io.github.loggerworld.domain.user_account.UserPropertyType
import io.github.loggerworld.domain.user_account.UserStatus
import io.github.loggerworld.dto.request.UserAddRequest
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class UserMapper : Mapper<UserAccount, UserAddRequest> {

    override fun from(source: UserAddRequest) =
        UserAccount(
            source.userName,
            source.password,
            source.displayName,
            UserStatus(UserStatuses.OFFLINE),
            Language(source.language)
        ).also {
            val userProperty = UserProperty(source.email, it, UserPropertyType(UserPropertyTypes.EMAIL))
            it.properties.add(userProperty)
        }
}