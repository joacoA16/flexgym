package com.flex.management.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.flex.management.entity.Socio;
import com.flex.management.repository.SocioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordatorioService {

    private final SocioRepository socioRepository;
    private final WhatsAppService whatsAppService; // 1. Inyectamos a nuestro cartero

    @Value("${whatsapp.api.template-name}")
    private String templateName;

   
    @Scheduled(cron = "0 20 16 * * ?")
    public void enviarRecordatoriosVencimiento() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Socio> sociosAVencer = socioRepository.findByFechaVencimientoCuota(manana);

        System.out.println("Iniciando envío de recordatorios automáticos. Socios a notificar: " + sociosAVencer.size());

        for (Socio socio : sociosAVencer) {
            if (socio.isActivo() && socio.getTelefono() != null) {
                
                // 2. Le delegamos el trabajo pesado al WhatsAppService
                whatsAppService.enviarMensajeTemplate(socio.getTelefono(), templateName);
                
                System.out.println("Solicitud de WhatsApp delegada para: " + socio.getNombre());
            }
        }
    }
    
    
}