package io.github.loggerworld.mapper.logging

import io.github.loggerworld.domain.logging.LogTypeMessage
import io.github.loggerworld.dto.inner.logging.MessageTemplateData
import io.github.loggerworld.mapper.Mapper
import org.springframework.stereotype.Service

@Service
class MessageTemplateDataMapper: Mapper<MessageTemplateData, LogTypeMessage> {

    override fun from(source: LogTypeMessage): MessageTemplateData {
        val result = MessageTemplateData()
        result.messageId = source.id!!

        source.logTypeMessageDescriptions.forEach {
            result.messages[it.language.code] = it.value
        }

        return result
    }
}