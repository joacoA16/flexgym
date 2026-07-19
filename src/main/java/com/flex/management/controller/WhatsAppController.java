package com.flex.management.controller;
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

    // 🔑 ESTA ES LA CONTRASEÑA QUE INVENTASTE (Debe coincidir con la página de Meta)
    private final String VERIFY_TOKEN = "gimnasio_secreto_123";

    // Meta usa este método GET solo una vez para validar que tu servidor es real
    @GetMapping("/webhook")
    public ResponseEntity<String> verificarWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            System.out.println("✅ Webhook de WhatsApp verificado exitosamente por Meta!");
            // Es obligatorio devolver el 'challenge' tal cual para que Meta lo apruebe
            return ResponseEntity.ok(challenge); 
        } else {
            System.out.println("❌ Intento de verificación fallido. Token incorrecto.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Por aquí entrarán los mensajes reales que te manden los socios en el futuro
    @PostMapping("/webhook")
    public ResponseEntity<Void> recibirMensajeWhatsApp(@RequestBody String payload) {
        System.out.println("📩 Nuevo evento de WhatsApp recibido:");
        System.out.println(payload);
        return ResponseEntity.ok().build();
    }
}