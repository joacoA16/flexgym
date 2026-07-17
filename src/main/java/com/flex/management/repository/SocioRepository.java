package com.flex.management.repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.management.entity.Socio;

public interface SocioRepository extends JpaRepository<Socio, String> {
    
    // Devuelve un Optional por si el DNI ingresado no existe
    Optional<Socio> findByDni(String dni);
    
    // Busca a todos los socios cuya cuota vence en la fecha exacta que le pasemos
    List<Socio> findByFechaVencimientoCuota(LocalDate fechaVencimiento);
    
    // Opcional: Para el profe, listar solo los socios activos
    List<Socio> findByActivoTrue();
}
