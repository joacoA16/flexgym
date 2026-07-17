package com.flex.management.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.management.entity.Configuracion;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
    Optional<Configuracion> findByClave(String clave);
}