package io.github.loggerworld.repository.geography

import io.github.loggerworld.domain.geography.Location
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository : JpaRepository<Location, Short>