package com.flex.management.service;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.token}")
    private String accessToken;

    public void enviarMensajeTemplate(String numeroDestino, String templateName) {
        // Herramienta de Spring para hacer peticiones HTTP
        RestTemplate restTemplate = new RestTemplate();
        
        // URL final: https://graph.facebook.com/v18.0/TU_PHONE_ID/messages
        String url = apiUrl + "/" + phoneNumberId + "/messages";

        // 1. Configuramos los Headers (Autorización y Tipo de contenido)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 2. Construimos el cuerpo del JSON (Payload) para Meta
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", numeroDestino);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName); 
        
        Map<String, String> language = new HashMap<>();
        language.put("code", "en_US"); // hello_world viene por defecto en en_US
        template.put("language", language);

        body.put("template", template);

        // 3. Empaquetamos todo y enviamos la petición POST
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Mensaje de WhatsApp enviado a " + numeroDestino);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar WhatsApp: " + e.getMessage());
            throw new RuntimeException("Fallo al comunicarse con Meta API");
        }
    }
}