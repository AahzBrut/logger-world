package io.github.loggerworld.repository.user_account

import io.github.loggerworld.domain.user_account.UserStatusDescription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserStatusDescriptionRepository : JpaRepository<UserStatusDescription, Short>