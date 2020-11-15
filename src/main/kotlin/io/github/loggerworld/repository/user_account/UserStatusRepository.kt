package io.github.loggerworld.repository.user_account

import io.github.loggerworld.domain.user_account.UserStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserStatusRepository : JpaRepository<UserStatus, Byte>