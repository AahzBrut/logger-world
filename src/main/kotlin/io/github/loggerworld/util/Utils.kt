package io.github.loggerworld.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger


interface LogAware

@Suppress("unused")
inline fun <reified T : LogAware> T.logger(): Logger =
    getLogger(T::class.java)


const val TOKEN_PREFIX = "Bearer "