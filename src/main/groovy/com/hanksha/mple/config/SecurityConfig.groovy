package com.hanksha.mple.config

import com.hanksha.mple.service.auth.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource;

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource

    @Autowired
    UserService userService

    @Autowired
    void configure(AuthenticationManagerBuilder auth) {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery('SELECT username, password, enabled FROM users WHERE username = ?')
                .authoritiesByUsernameQuery('SELECT username, role FROM user_roles WHERE username = ?')
    }

    @Override
    protected void configure(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers('/', '/mple/info', '/plugins/**').permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
                    void handle(
                            HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    }
                })
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    void commence(
                            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    }
                })
                .and()
            .formLogin()
                .loginProcessingUrl('/login')
                .successHandler(userService)
                .failureHandler(new AuthenticationFailureHandler() {
                    void onAuthenticationFailure(
                            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                    }
                })
                .usernameParameter('username').passwordParameter('password')
                .permitAll()
                .and()
            .logout()
                .logoutUrl('/logout')
                .logoutSuccessHandler(userService)
                .and()
            .sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry())
    }

    @Override
    void configure(WebSecurity web) {
    }

    @Bean
    SessionRegistry sessionRegistry() {
        new SessionRegistryImpl()
    }

}
