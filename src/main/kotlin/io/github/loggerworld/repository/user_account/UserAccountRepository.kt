package io.github.loggerworld.repository.user_account

import io.github.loggerworld.domain.user_account.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface UserAccountRepository : JpaRepository<UserAccount, Long>