package com.flex.management.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.flex.management.entity.Socio;
import com.flex.management.repository.SocioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordatorioService {

    private final SocioRepository socioRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // Para hacer peticiones HTTP POST

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Value("${whatsapp.api.token}")
    private String apiToken;

    @Value("${whatsapp.api.template-name}")
    private String templateName;

    // Ejecuta la tarea de lunes a domingo a las 10:00 AM
    // Formato de la expresión Cron: Segundos Minutos Horas DíaMes Mes DíaSemana
    @Scheduled(cron = "0 20 16 * * ?")
    public void enviarRecordatoriosVencimiento() {
        // 1. Calcular la fecha de mañana
        LocalDate manana = LocalDate.now().plusDays(1);

        // 2. Buscar los socios que vencen exactamente mañana
        List<Socio> sociosAVencer = socioRepository.findByFechaVencimientoCuota(manana);

        System.out.println("Iniciando envío de recordatorios automáticos. Socios a notificar: " + sociosAVencer.size());

        // 3. Iterar y enviar el mensaje a cada socio
        for (Socio socio : sociosAVencer) {
            if (socio.isActivo() && socio.getTelefono() != null) {
                enviarMensajeWhatsApp(socio);
            }
        }
    }

   private void enviarMensajeWhatsApp(Socio socio) {
    String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiToken);

    Map<String, Object> body = new HashMap<>();
    body.put("messaging_product", "whatsapp");
    body.put("to", socio.getTelefono()); 
    body.put("type", "template");

    Map<String, Object> template = new HashMap<>();
    template.put("name", templateName);

    Map<String, String> language = new HashMap<>();
    language.put("code", "en_US"); // <-- Cambiamos temporalmente a inglés para hello_world
    template.put("language", language);

    // ⚠️ COMENTAMOS TODO ESTE BLOQUE TEMPORALMENTE (porque hello_world no tiene variables)
    /*
    List<Map<String, Object>> components = new ArrayList<>();
    Map<String, Object> bodyComponent = new HashMap<>();
    bodyComponent.put("type", "body");

    List<Map<String, String>> parameters = new ArrayList<>();
    Map<String, String> paramNombre = new HashMap<>();
    paramNombre.put("type", "text");
    paramNombre.put("text", socio.getNombre());
    parameters.add(paramNombre);

    bodyComponent.put("parameters", parameters);
    components.add(bodyComponent);
    template.put("components", components);
    */

    body.put("template", template);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("WhatsApp enviado con éxito a: " + socio.getNombre());
        }
    } catch (Exception e) {
        System.err.println("Error al enviar WhatsApp a " + socio.getNombre() + ": " + e.getMessage());
    }
}
}
