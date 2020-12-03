package io.github.loggerworld.mapper

interface Mapper<T, S> {

    fun from(source: S): T {
        TODO("not implemented")
    }

    fun from(source: List<S>): List<T> =
        source.map { from(it) }.toList()

    fun fromList(source: List<S>): T {
        TODO("not implemented")
    }
}