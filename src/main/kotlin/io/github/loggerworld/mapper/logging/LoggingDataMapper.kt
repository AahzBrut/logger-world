package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.logging.LogClass
import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class LoggingDataMapper(
    private val logTypesDataMapper: LogTypesDataMapper
): Mapper<LoggingData, LogClass> {

    override fun fromList(sources: List<LogClass>): LoggingData {
        val response = LoggingData()

        sources.forEach {
            response.classes[it.code] = logTypesDataMapper.fromList(it.logTypes)
        }

        return response
    }
}