package com.flex.management.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flex.management.Enum.Estados;
import com.flex.management.Enum.MetodosPagos;
import com.flex.management.entity.Pago;
import com.flex.management.entity.Socio;
import com.flex.management.repository.PagoRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class PagoService {

    
    private final PagoRepository pagoRepository;

    
    private final SocioService socioService;

    @Transactional
    public Pago registrarPagoEfectivo(String dniSocio, Double monto) {
        Socio socio = socioService.buscarPorDni(dniSocio);

        Pago nuevoPago = new Pago();
        nuevoPago.setSocio(socio);
        nuevoPago.setMonto((double) (monto == null ? 0L : Math.round(monto * 100)));
        nuevoPago.setMetodoPago(MetodosPagos.EFECTIVO);
        nuevoPago.setEstado(Estados.ACEPTADO);
        nuevoPago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(nuevoPago);
        socioService.extenderCuotaUnMes(dniSocio);

        return pagoGuardado;
    }

 @Transactional
    public void procesarPagoAprobadoMercadoPago(String dniSocio, Double monto, Long idMercadoPago) {
      // 1. ESCUDO ANTIDUPLICADOS: Verificamos si ya registramos este pago
        if (pagoRepository.existsByIdMercadoPago(idMercadoPago)) {
            System.out.println("🛡️ El pago " + idMercadoPago + " ya fue procesado anteriormente. Se omite para evitar doble cobro.");
            return; // Cortamos la ejecución aquí mismo
        }

        Socio socio = socioService.buscarPorDni(dniSocio);
        
        Pago nuevoPago = new Pago();
        nuevoPago.setSocio(socio);
        nuevoPago.setMonto(monto);
        nuevoPago.setMetodoPago(MetodosPagos.MERCADO_PAGO);
        nuevoPago.setEstado(Estados.ACEPTADO);
        nuevoPago.setFechaPago(LocalDateTime.now());
        
        nuevoPago.setIdMercadoPago(idMercadoPago); // <--- Guardamos el ID oficial

        pagoRepository.save(nuevoPago);
        socioService.extenderCuotaUnMes(dniSocio);
}
}