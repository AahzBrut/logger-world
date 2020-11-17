package io.github.loggerworld.service.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.loggerworld.dto.request.UserLoginRequest
import io.github.loggerworld.service.UserService
import io.github.loggerworld.util.TOKEN_PREFIX
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class JwtAuthenticationFilter(
    authManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtService: JwtService
) : UsernamePasswordAuthenticationFilter(authManager) {

    @Value("\${logger-world.login.endpoint}")
    private lateinit var loginUrl: String

    @PostConstruct
    private fun init() {
        setFilterProcessesUrl(loginUrl)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {

        val userLoginRequest : UserLoginRequest = ObjectMapper().readValue(request.inputStream, UserLoginRequest::class.java)

        return if (userService.authenticate(userLoginRequest)) {
            UsernamePasswordAuthenticationToken(
                userLoginRequest.userName,
                null,
                emptyList())
        } else {
            response.status = HttpStatus.UNAUTHORIZED.ordinal
            throw RuntimeException("Authentication failed for user ${userLoginRequest.userName}")
        }

    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?, authResult: Authentication?) {
        authResult?.let {
            val token = jwtService.generateToken(authResult.name)
            response.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + token)
        }
    }
}