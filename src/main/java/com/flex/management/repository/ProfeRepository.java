package com.flex.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.management.entity.Profe;

public interface ProfeRepository extends JpaRepository<Profe, String> {
    
    // Necesario para que Spring Security valide las credenciales
    Optional<Profe> findByDni(String dni);
}
