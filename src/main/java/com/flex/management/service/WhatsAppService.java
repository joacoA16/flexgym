package com.flex.management.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // 1. Agregamos nombreSocio como tercer parámetro
    public void enviarMensajeTemplate(String numeroDestino, String templateName, String nombreSocio) {
        RestTemplate restTemplate = new RestTemplate();
        
        String url = apiUrl + "/" + phoneNumberId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", numeroDestino);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName); 
        
        Map<String, String> language = new HashMap<>();
        language.put("code", "es_AR"); 
        template.put("language", language);

        // ==========================================
        // 2. SECCIÓN DE COMPONENTES (Variables)
        // ==========================================
        List<Map<String, Object>> components = new ArrayList<>();
        
        // Indicamos que la variable va en el cuerpo (body) del mensaje
        Map<String, Object> bodyComponent = new HashMap<>();
        bodyComponent.put("type", "body"); 

        // Creamos la lista de parámetros (en este caso, solo uno)
        List<Map<String, String>> parameters = new ArrayList<>();
        Map<String, String> paramNombre = new HashMap<>();
        paramNombre.put("type", "text");
        paramNombre.put("text", nombreSocio); // Reemplaza {{1}} con el nombre
        
        parameters.add(paramNombre);
        bodyComponent.put("parameters", parameters);
        components.add(bodyComponent);
        
        // Enganchamos los componentes al template
        template.put("components", components);
        // ==========================================

        body.put("template", template);

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