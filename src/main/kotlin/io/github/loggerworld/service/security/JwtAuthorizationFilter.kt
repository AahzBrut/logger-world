package io.github.loggerworld.service.security

import io.github.loggerworld.util.TOKEN_PREFIX
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Service
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class JwtAuthorizationFilter(
    private val authManager: AuthenticationManager
) : BasicAuthenticationFilter(authManager) {


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        val auth = authManager.authenticate(UsernamePasswordAuthenticationToken(null, authHeader))

        SecurityContextHolder.getContext().authentication = auth
        chain.doFilter(request, response)
    }
}