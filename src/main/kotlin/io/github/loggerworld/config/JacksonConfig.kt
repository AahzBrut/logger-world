package io.github.loggerworld.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class JacksonConfig(
    private val objectMapper: ObjectMapper
) {

    @PostConstruct
    fun initModules(){
        objectMapper.registerModule(JavaTimeModule())
    }
}