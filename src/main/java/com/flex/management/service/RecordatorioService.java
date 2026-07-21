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
    private final WhatsAppService whatsAppService;

    @Value("${whatsapp.api.template-name}")
    private String templateName;

  
    @Scheduled(cron = "0 26 10 * * ?", zone = "America/Argentina/Buenos_Aires")
    public void enviarRecordatoriosVencimiento() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Socio> sociosAVencer = socioRepository.findByFechaVencimientoCuota(manana);

        System.out.println("Iniciando envío de recordatorios automáticos. Socios a notificar: " + sociosAVencer.size());

        for (Socio socio : sociosAVencer) {
            if (socio.isActivo() && socio.getTelefono() != null) {
                
                // 🔥 EL ARREGLO ESTÁ AQUÍ: Ahora le pasamos el teléfono, la plantilla y el NOMBRE del socio
                whatsAppService.enviarMensajeTemplate(socio.getTelefono(), templateName, socio.getNombre());
                
                System.out.println("Solicitud de WhatsApp delegada para: " + socio.getNombre());
            }
        }
    }
}