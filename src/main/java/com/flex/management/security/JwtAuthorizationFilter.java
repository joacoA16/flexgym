package com.flex.management.security;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Extraer el header "Authorization"
        String header = request.getHeader("Authorization");

        // 2. Verificar que exista y tenga el prefijo "Bearer "
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token limpio
        String token = header.replace("Bearer ", "");

        // 4. Validar el token
        if (jwtUtil.isTokenValid(token)) {
            Claims claims = jwtUtil.getClaims(token);
            String dni = claims.getSubject();
            String rol = claims.get("rol", String.class);

            // 5. Crear la autoridad para Spring Security (El formato exige el prefijo ROLE_)
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));

            // 6. Autenticar al usuario en el contexto actual
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(dni, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 7. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}