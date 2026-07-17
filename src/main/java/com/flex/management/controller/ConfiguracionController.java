package com.flex.management.controller;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flex.management.service.ConfiguracionService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

   
    private final ConfiguracionService configuracionService;

    @GetMapping("/precio")
    public ResponseEntity<Double> getPrecioActual() {
        return ResponseEntity.ok(configuracionService.obtenerPrecioCuota());
    }

    @PutMapping("/precio")
    public ResponseEntity<String> updatePrecio(@RequestBody Map<String, Double> payload) {
        Double nuevoPrecio = payload.get("precio");
        configuracionService.actualizarPrecioCuota(nuevoPrecio);
        return ResponseEntity.ok("Precio actualizado correctamente.");
    }
}