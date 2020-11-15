package io.github.loggerworld.repository.user_account

import io.github.loggerworld.domain.user_account.UserPropertyTypeDescription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPropertyTypeDescriptionRepository : JpaRepository<UserPropertyTypeDescription, Int>