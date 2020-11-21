package io.github.loggerworld.config

import io.github.loggerworld.controller.SIGN_UP_URL
import io.github.loggerworld.controller.TEST_URL
import io.github.loggerworld.service.security.JwtAuthenticationFilter
import io.github.loggerworld.service.security.JwtAuthorizationFilter
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthorizationFilter: JwtAuthorizationFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .headers().frameOptions().disable()
            .and().cors().and().csrf().disable()
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
}