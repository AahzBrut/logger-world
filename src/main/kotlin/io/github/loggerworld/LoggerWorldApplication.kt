package io.github.loggerworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LoggerWorldApplication

fun main(args: Array<String>) {
    runApplication<LoggerWorldApplication>(*args)
}
