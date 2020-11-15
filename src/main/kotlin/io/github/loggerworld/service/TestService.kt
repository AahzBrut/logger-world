package io.github.loggerworld.service

import io.github.loggerworld.repository.main.LanguageRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TestService(
    private val repository: LanguageRepository
) {

    private val logger by lazy { LoggerFactory.getLogger(TestService::class.java) }

    @PostConstruct
    fun test() {
        val languages = repository.findAll()

        languages.forEach { logger.info(it.name) }
    }
}