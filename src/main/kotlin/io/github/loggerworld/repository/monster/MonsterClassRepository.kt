package io.github.loggerworld.repository.monster

import io.github.loggerworld.domain.monster.MonsterClass
import org.springframework.data.jpa.repository.JpaRepository

interface MonsterClassRepository : JpaRepository<MonsterClass, Byte>