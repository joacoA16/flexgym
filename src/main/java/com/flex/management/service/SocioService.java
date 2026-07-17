package com.flex.management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flex.management.entity.Socio;
import com.flex.management.repository.SocioRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class SocioService {

  
    private final SocioRepository socioRepository;

    public List<Socio> obtenerTodos() {
        return socioRepository.findAll();
    }

    public Socio buscarPorDni(String dni) {
        return socioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con DNI: " + dni));
    }

    public Socio guardarSocio(Socio socio) {
        return socioRepository.save(socio);
    }

    @Transactional
    public void extenderCuotaUnMes(String dni) {
        Socio socio = buscarPorDni(dni);

        LocalDate fechaActual = LocalDate.now();
        LocalDate vencimientoActual = socio.getFechaVencimientoCuota();

        if (vencimientoActual == null || vencimientoActual.isBefore(fechaActual)) {
            socio.setFechaVencimientoCuota(fechaActual.plusMonths(1));
        } else {
            socio.setFechaVencimientoCuota(vencimientoActual.plusMonths(1));
        }

        socioRepository.save(socio);
    }
}
