package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.logging.LogType
import io.github.loggerworld.dto.inner.logging.LogTypesData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class LogTypesDataMapper(
    private val logTypeDataMapper: LogTypeDataMapper
): Mapper<LogTypesData, LogType> {

    override fun fromList(sources: List<LogType>): LogTypesData {
        val result = LogTypesData()

        sources.forEach {
            result.types[it.code] = logTypeDataMapper.from(it)
        }

        return result
    }
}