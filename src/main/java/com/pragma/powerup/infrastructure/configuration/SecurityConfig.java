package com.pragma.powerup.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Deshabilitamos CSRF para poder hacer POST desde Postman
                .authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/users/**").permitAll()
                .anyRequest().permitAll(); // Permitimos todas las peticiones por ahora


        return http.build();
    }
}