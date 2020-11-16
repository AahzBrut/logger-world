package io.github.loggerworld.mapper

interface Mapper<T, S> {

    fun from(source: S): T
    fun from(source: List<S>): List<T> =
        source.map { from(it) }.toList()
}