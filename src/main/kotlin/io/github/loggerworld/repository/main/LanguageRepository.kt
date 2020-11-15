package io.github.loggerworld.repository.main

import io.github.loggerworld.domain.main.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface LanguageRepository : JpaRepository<Language, Short>