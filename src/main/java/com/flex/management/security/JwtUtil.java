package com.flex.management.security;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Component
public class JwtUtil {

    // Clave secreta (Debe ser de al menos 256 bits). En producción, ¡usa variables de entorno!
    private static final String SECRET = "GimnasioSuperSecretoKeyParaFirmaJWT2026123456789";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    // Tiempo de expiración (ej. 24 horas)
    private static final long EXPIRATION_TIME = 86400000;

    // Generar Token
    public String generateToken(String dni, String rol) {
        return Jwts.builder()
                .setSubject(dni)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validar y obtener los datos (Claims) del token
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            // Si la firma es inválida, expiró, o está mal formado, cae aquí.
            return false;
        }
    }
}
