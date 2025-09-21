package com.devsdoagi.agricripto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desativa CSRF (não precisa agora)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // libera todas as rotas
                );
        return http.build();
    }
}