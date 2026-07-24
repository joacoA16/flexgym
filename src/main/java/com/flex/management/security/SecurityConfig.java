package com.flex.management.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
 @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/pagos/webhook").permitAll()
                
                .requestMatchers("/pago-exitoso", "/pago-pendiente", "/pago-fallido", "/pagoexitoso", "/pagopendiente", "/pagofallido").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/", "/*.html", "/*.css", "/*.js","/**/*.webp").permitAll()
                .requestMatchers("/api/auth/**", "/pago-exitoso", "/pago-pendiente", "/pago-fallido", "/pagoexitoso", "/pagopendiente", "/pagofallido", "/api/pagos/webhook","/api/whatsapp/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/socios/buscar").hasAnyRole("SOCIO", "ADMIN")
                .requestMatchers("/api/pagos/mercado-pago/**").hasRole("SOCIO")
                .requestMatchers("/api/socios/**").hasRole("ADMIN")
                .requestMatchers("/api/pagos/efectivo").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        // Instalar nuestro filtro personalizado antes del filtro por defecto de Spring
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        // Aquí más adelante engancharemos nuestro filtro personalizado para leer los JWT

        // http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean para encriptar las contraseñas de los profesores usando BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}