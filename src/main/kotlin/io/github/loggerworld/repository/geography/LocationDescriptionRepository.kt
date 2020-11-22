package io.github.loggerworld.repository.geography

import io.github.loggerworld.domain.geography.LocationDescription
import org.springframework.data.jpa.repository.JpaRepository

interface LocationDescriptionRepository: JpaRepository<LocationDescription, Int>