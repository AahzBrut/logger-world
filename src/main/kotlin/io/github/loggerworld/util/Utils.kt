package io.github.loggerworld.util

import com.badlogic.ashley.core.Entity
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger


interface LogAware

@Suppress("unused")
inline fun <reified T : LogAware> T.logger(): Logger =
    getLogger(T::class.java)


const val TOKEN_PREFIX = "Bearer "

val emptyEntity = Entity()

const val nanoSecond = 1 / 1_000_000_000f