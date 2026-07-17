package com.flex.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flex.management.entity.Pago;
import com.flex.management.entity.Socio;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findBySocio(Socio socio);

    List<Pago> findBySocioOrderByFechaPagoDesc(Socio socio);
    boolean existsByIdMercadoPago(Long idMercadoPago);
    
}