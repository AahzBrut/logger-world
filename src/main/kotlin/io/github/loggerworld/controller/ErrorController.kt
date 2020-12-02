package io.github.loggerworld.controller

import io.github.loggerworld.dto.response.ResponseObject
import io.github.loggerworld.exception.EmailAlreadyExistsException
import io.github.loggerworld.exception.UserAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException

@ControllerAdvice
class ErrorController : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [UserAlreadyExistsException::class, EmailAlreadyExistsException::class])
    fun errorHandler(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {

        return ResponseEntity(ResponseObject(false, ex.message ?: ""), HttpStatus.BAD_REQUEST)
    }
}