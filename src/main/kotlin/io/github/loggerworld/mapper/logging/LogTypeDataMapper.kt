package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.logging.LogType
import io.github.loggerworld.dto.inner.logging.LogTypeData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class LogTypeDataMapper(
    private val messageTemplateDataMapper: MessageTemplateDataMapper
): Mapper<LogTypeData, LogType> {

    override fun from(source: LogType): LogTypeData {
        val result = LogTypeData()
        result.numValues = source.numVals
        source.logTypeMessages.forEach {
            result.templates[it.ordinal] = messageTemplateDataMapper.from(it)
        }

        return result
    }
}