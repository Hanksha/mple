package com.hanksha.mple.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

/**
 * Created by vivien on 8/23/16.
 */
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.jdbcAuthentication()
            .usersByUsernameQuery('SELECT username, password, enabled FROM users WHERE username = ?')
            .authoritiesByUsernameQuery('SELECT username, role FROM user_roles WHERE username = ?')
    }

    @Override
    protected void configure(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeRequests()
                .anyRequest().permitAll()
                .and()
            .formLogin()
            .   defaultSuccessUrl("/")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
    }

    @Override
    void configure(WebSecurity web) {
    }
}
