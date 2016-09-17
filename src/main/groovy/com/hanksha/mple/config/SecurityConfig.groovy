package com.hanksha.mple.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource;

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource

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
                .antMatchers('/', '/plugins/**').permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginProcessingUrl('/login')
                .successHandler(new AuthenticationSuccessHandler() {
                    void onAuthenticationSuccess(
                            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        // temp fix to avoid redirect
                    }
                })
                .usernameParameter('username').passwordParameter('password')
                .permitAll()
                .and()
            .logout()
                .logoutUrl('/logout')
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    void onLogoutSuccess(
                            HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        // temp fix to avoid redirect
                    }
                })

    }

    @Override
    void configure(WebSecurity web) {
    }
}
