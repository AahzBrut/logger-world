package io.github.loggerworld.repository.monster

import io.github.loggerworld.domain.monster.MonsterType
import org.springframework.data.jpa.repository.JpaRepository

interface MonsterTypeRepository : JpaRepository<MonsterType, Byte>