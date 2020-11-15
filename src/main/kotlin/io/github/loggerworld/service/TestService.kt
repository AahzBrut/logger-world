package io.github.loggerworld.service

import io.github.loggerworld.repository.main.LanguageRepository
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TestService(
    private val repository: LanguageRepository
) : LogAware {

    @PostConstruct
    fun test() {
        val languages = repository.findAll()

        languages.forEach { logger().info(it.name) }
    }
}