package io.github.loggerworld.service.domain

import io.github.loggerworld.dto.inner.logging.LoggingData
import io.github.loggerworld.mapper.logging.LoggingDataMapper
import io.github.loggerworld.repository.logging.LogClassRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class LoggingDomainService(
    private val logClassRepository: LogClassRepository,
    private val loggingDataMapper: LoggingDataMapper,
) {

    @Transactional
    fun getLogMessagesSettings(): LoggingData {

        return loggingDataMapper.fromList(logClassRepository.findAll())
    }
}