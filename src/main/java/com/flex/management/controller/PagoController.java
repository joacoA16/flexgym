package com.flex.management.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flex.management.entity.Pago;
import com.flex.management.entity.Socio;
import com.flex.management.service.ConfiguracionService;
import com.flex.management.service.MercadoPagoService;
import com.flex.management.service.PagoService;
import com.flex.management.service.SocioService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {
private final ConfiguracionService configuracionService;
    private final PagoService pagoService;
    private final MercadoPagoService mercadoPagoService;
    private final SocioService socioService; // <-- NUEVO: Necesitamos acceder a los datos del socio

   

    @PostMapping("/efectivo/{dni}")
    public ResponseEntity<?> registrarPagoEfectivo(@PathVariable String dni) {
        try {
            // NUEVO: Validación de 24 horas para cobros en caja
            Socio socio = socioService.buscarPorDni(dni);
          if (socio.getFechaVencimientoCuota() != null) {
    // Si hoy es lunes, el límite para permitir pagar es el martes (hoy + 1 día)
    LocalDate limiteParaPagar = LocalDate.now().plusDays(1); 
    
    // Si el vencimiento es después de mañana, bloqueamos el pago
    if (socio.getFechaVencimientoCuota().isAfter(limiteParaPagar)) {
        return ResponseEntity.badRequest().body("Tu cuota aún está vigente. Solo puedes renovarla cuando falten 24 horas o menos para su vencimiento.");
    }
}

            Double precioActual = configuracionService.obtenerPrecioCuota();
            Pago pagoGuardado = pagoService.registrarPagoEfectivo(dni, precioActual);
            
            return ResponseEntity.ok("Pago en efectivo registrado correctamente. Id de transacción: " + pagoGuardado.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar el pago: " + e.getMessage());
        }
    }

    // Cambiamos ResponseEntity<Map<...>> por ResponseEntity<?> para poder devolver un String si hay error
    @PostMapping("/mercado-pago/link")
    public ResponseEntity<?> generarLinkPago(@RequestBody Map<String, Object> payload) {
        try {
            String dniSocio = (String) payload.get("dniSocio");
            Socio socio = socioService.buscarPorDni(dniSocio);

            // NUEVO: Validación de 24 horas para Mercado Pago
           if (socio.getFechaVencimientoCuota() != null) {
    // Si hoy es lunes, el límite para permitir pagar es el martes (hoy + 1 día)
    LocalDate limiteParaPagar = LocalDate.now().plusDays(1); 
    
    // Si el vencimiento es después de mañana, bloqueamos el pago
    if (socio.getFechaVencimientoCuota().isAfter(limiteParaPagar)) {
        return ResponseEntity.badRequest().body("Tu cuota aún está vigente. Solo puedes renovarla cuando falten 24 horas o menos para su vencimiento.");
    }
}

            Double monto = configuracionService.obtenerPrecioCuota();

            // Llama al servicio que se comunica con la API
            String linkDePago = mercadoPagoService.crearPreferenciaPago(dniSocio, monto);

            // Devuelve el link en formato JSON para que el frontend redirija al usuario
            return ResponseEntity.ok(Collections.singletonMap("url", linkDePago));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar el pago: " + e.getMessage());
        }
    }

   @PostMapping("/webhook")
public ResponseEntity<Void> recibirNotificacionMercadoPago(
        HttpServletRequest request,
        @RequestBody(required = false) String payload) { // Recibimos texto crudo, imposible que de 400
        
    System.out.println("\n========== 🔔 WEBHOOK RECIBIDO ==========");
    System.out.println("Query de la URL: " + request.getQueryString());
    System.out.println("Payload Crudo: " + payload);
    
    try {
        Long paymentId = null;
        
        // 1. Buscamos en la URL (Formato IPN)
        String dataId = request.getParameter("data.id");
        String paramId = request.getParameter("id");
        String topic = request.getParameter("topic");
        String type = request.getParameter("type");

        if (dataId != null) {
            paymentId = Long.valueOf(dataId);
        } else if (paramId != null && ("payment".equals(topic) || "payment".equals(type))) {
            paymentId = Long.valueOf(paramId);
        }
        
        // 2. Si no vino en la URL, leemos el JSON manualmente (Formato Webhook)
        if (paymentId == null && payload != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);
            
            if (node.has("data") && node.get("data").has("id")) {
                paymentId = node.get("data").get("id").asLong();
            }
        }
        
        // 3. Procesamos el pago si encontramos el ID
        if (paymentId != null) {
            System.out.println("▶ ¡Payment ID encontrado!: " + paymentId);
            mercadoPagoService.verificarYAprobarPago(paymentId);
        } else {
            System.out.println("⚠️ Notificación ignorada (no es un pago o no tiene ID).");
        }
        
    } catch (Exception e) {
         System.err.println("❌ Error interno procesando webhook: " + e.getMessage());
    }
    
    System.out.println("=========================================\n");
    return ResponseEntity.ok().build(); // Siempre devuelve 200 para que MP deje de insistir
}
}