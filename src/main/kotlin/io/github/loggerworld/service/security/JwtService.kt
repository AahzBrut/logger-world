package io.github.loggerworld.service.security

import io.github.loggerworld.util.TOKEN_PREFIX
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class JwtService {


    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private lateinit var expirationTime: String

    fun extractUsername(authToken: String): String? {
        return getClaimsFromToken(authToken)
            .subject
    }

    fun getClaimsFromToken(authToken: String): Claims {
        val realToken = authToken.drop(TOKEN_PREFIX.length)
        val key: String = Base64.getEncoder().encodeToString(secret.toByteArray())
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(realToken)
            .body
    }

    fun validateToken(authToken: String): Boolean {
        return getClaimsFromToken(authToken)
            .expiration
            .after(Date())
    }

    fun generateToken(userName: String): String {
        val claims = HashMap<String, Any?>()
        val creationDate = Date()
        val expirationDate = Date(creationDate.time + expirationTime.toLong())
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userName)
            .setIssuedAt(creationDate)
            .setExpiration(expirationDate)
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()))
            .compact()
    }
}