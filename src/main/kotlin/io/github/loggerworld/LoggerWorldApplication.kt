package io.github.loggerworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import javax.annotation.PostConstruct

@EnableScheduling
@SpringBootApplication
class LoggerWorldApplication

fun main(args: Array<String>) {
    runApplication<LoggerWorldApplication>(*args)
}
