package com.flex.management.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    // 🔑 Inyectamos el token desde las variables de entorno (.env o Render)
    @Value("${whatsapp.verify-token}")
    private String verifyToken;

    @GetMapping("/webhook")
    public ResponseEntity<String> verificarWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        // Comparamos el token que manda Meta con el que tienes en tu variable de entorno
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            System.out.println("✅ Webhook de WhatsApp verificado exitosamente por Meta!");
            return ResponseEntity.ok(challenge); 
        } else {
            System.out.println("❌ Intento de verificación fallido. Token incorrecto.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirMensajeWhatsApp(@RequestBody String payload) {
        System.out.println("📩 Nuevo evento de WhatsApp recibido:");
        System.out.println(payload);
        return ResponseEntity.ok().build();
    }
}