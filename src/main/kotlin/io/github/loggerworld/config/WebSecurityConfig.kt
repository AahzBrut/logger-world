package io.github.loggerworld.config

import io.github.loggerworld.controller.SIGN_UP_URL
import io.github.loggerworld.controller.TEST_URL
import io.github.loggerworld.service.security.JwtAuthenticationFilter
import io.github.loggerworld.service.security.JwtAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthorizationFilter: JwtAuthorizationFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .headers().frameOptions().disable()
            .and().cors()
            .configurationSource(corsConfigurationSource())
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(SIGN_UP_URL).permitAll()
            .antMatchers(TEST_URL).authenticated()
            .antMatchers("/chat").authenticated()
            .antMatchers("/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(jwtAuthenticationFilter)
            .addFilter(jwtAuthorizationFilter)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
        //the below three lines will add the relevant CORS response headers
        configuration.addAllowedOrigin("*")
        configuration.addAllowedHeader("*")
        configuration.addAllowedMethod("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}