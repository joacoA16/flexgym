package com.flex.management.service;
import org.springframework.stereotype.Service;

import com.flex.management.entity.Configuracion;
import com.flex.management.repository.ConfiguracionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    
    private final ConfiguracionRepository configuracionRepository;

    private final String CLAVE_PRECIO = "PRECIO_CUOTA";

    public Double obtenerPrecioCuota() {
        return configuracionRepository.findByClave(CLAVE_PRECIO)
                .map(Configuracion::getValor)
                .orElse(15000.0); // Valor por defecto si nadie lo configuró aún
    }

    public void actualizarPrecioCuota(Double nuevoPrecio) {
        Configuracion config = configuracionRepository.findByClave(CLAVE_PRECIO)
                .orElse(new Configuracion());
        
        config.setClave(CLAVE_PRECIO);
        config.setValor(nuevoPrecio);
        
        configuracionRepository.save(config);
    }
}