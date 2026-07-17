package com.flex.management.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flex.management.entity.Profe;
import com.flex.management.entity.Socio;
import com.flex.management.repository.ProfeRepository;
import com.flex.management.repository.SocioRepository;
import com.flex.management.security.JwtUtil;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    
    private final ProfeRepository profeRepository;

    
    private final SocioRepository socioRepository;

    
    private final PasswordEncoder passwordEncoder;

   
private final JwtUtil jwtUtil;

@PostMapping("/profe")
    public ResponseEntity<?> loginProfe(@RequestBody Map<String, String> credentials) {
        String dni = credentials.get("dni");
        String password = credentials.get("password");

        // 1. Buscamos al profe, pero lo guardamos en un Optional en lugar de lanzar la excepción
        Optional<Profe> profeOptional = profeRepository.findByDni(dni);

        // 2. Si el Optional está vacío (no existe el DNI) O la clave no coincide, devolvemos 401
        if (profeOptional.isEmpty() || !passwordEncoder.matches(password, profeOptional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        // 3. Si pasamos el filtro anterior, sacamos al profe del Optional de forma segura
        Profe profe = profeOptional.get();

        String token = jwtUtil.generateToken(profe.getDni(), "ROLE_ADMIN");
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("rol", "ROLE_ADMIN");
        response.put("nombre", profe.getNombre());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/socio")
    public ResponseEntity<?> loginSocio(@RequestBody Map<String, String> payload) {
        String dni = payload.get("dni");

        Optional<Socio> socioOptional = socioRepository.findByDni(dni);

        if (socioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El DNI ingresado no corresponde a un socio existente");
        }

        Socio socio = socioOptional.get();
//revisar logica de acceso en caso de vencimiento
        if (!socio.isActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El socio se encuentra inactivo");
        }
String token = jwtUtil.generateToken(socio.getDni(), "ROLE_SOCIO");

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("rol", "ROLE_SOCIO");
        response.put("nombre", socio.getNombre() + " " + socio.getApellido());

        return ResponseEntity.ok(response);
    }
}