package io.github.loggerworld.service.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthenticationManagerImpl(
    private val jwtService: JwtService
) : AuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication? {
        val authToken = authentication.credentials.toString()

        val userName: String? = jwtService.extractUsername(authToken)

        return if (userName!=null && jwtService.validateToken(authToken)) {
            UsernamePasswordAuthenticationToken(
                userName,
                null,
                emptyList())
        } else {
            null
        }
    }
}