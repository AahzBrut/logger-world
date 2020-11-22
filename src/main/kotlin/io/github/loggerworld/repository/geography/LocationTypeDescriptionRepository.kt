package io.github.loggerworld.repository.geography

import io.github.loggerworld.domain.geography.LocationTypeDescription
import org.springframework.data.jpa.repository.JpaRepository

interface LocationTypeDescriptionRepository : JpaRepository<LocationTypeDescription, Int>