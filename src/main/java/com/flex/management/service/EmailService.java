package com.flex.management.service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.flex.management.DTO.EjercicioDto;
import com.flex.management.DTO.RutinaDto;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarRutinaHtml(String emailSocio, String nombreSocio, RutinaDto rutina) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(emailSocio);
            helper.setSubject("💪 ¡Tu nueva rutina [" + rutina.getNombreRutina() + "] está lista!");
            
            // 1. Construir las filas de la tabla de ejercicios dinámicamente
            StringBuilder filasEjercicios = new StringBuilder();
            for (EjercicioDto ej : rutina.getEjercicios()) {
                filasEjercicios.append("<tr style='border-bottom: 1px solid #dddddd;'>")
                    .append("<td style='padding: 12px; text-align: left;'><strong>").append(ej.getNombre()).append("</strong></td>")
                    .append("<td style='padding: 12px; text-align: center;'>").append(ej.getSeries()).append("</td>")
                    .append("<td style='padding: 12px; text-align: center;'>").append(ej.getRepeticiones()).append("</td>")
                    .append("</tr>");
            }

            // 2. Armar la plantilla HTML completa con estilos inline (esenciales para correos)
            String contenidoHtml = "<html><body style='font-family: Arial, sans-serif; color: #333333; line-height: 1.6;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #f0f0f0; border-radius: 8px;'>"
                    + "  <h2 style='color: #1a73e8;'>¡Hola, " + nombreSocio + "!</h2>"
                    + "  <p>Tu profesor ha preparado una nueva rutina de entrenamiento especialmente para vos:</p>"
                    + "  <h3 style='background-color: #f1f3f4; padding: 10px; border-left: 5px solid #1a73e8; margin-top: 20px;'>"
                    + "    " + rutina.getNombreRutina() + ""
                    + "  </h3>"
                    + "  <table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>"
                    + "    <thead>"
                    + "      <tr style='background-color: #1a73e8; color: #ffffff;'>"
                    + "        <th style='padding: 12px; text-align: left;'>Ejercicio</th>"
                    + "        <th style='padding: 12px; text-align: center;'>Series</th>"
                    + "        <th style='padding: 12px; text-align: center;'>Repeticiones</th>"
                    + "      </tr>"
                    + "    </thead>"
                    + "    <tbody>"
                    +        filasEjercicios.toString()
                    + "    </tbody>"
                    + "  </table>"
                    + "  <br>"
                    + "  <p style='font-size: 12px; color: #777777; text-align: center; margin-top: 30px;'>"
                    + "    Este es un correo automático enviado por el sistema de gestión del Gimnasio."
                    + "  </p>"
                    + "</div>"
                    + "</body></html>";

            helper.setText(contenidoHtml, true);
            mailSender.send(mimeMessage);
            System.out.println("📧 Correo con rutina enviado con éxito a: " + emailSocio);

        } catch (Exception e) {
            throw new RuntimeException("Error al estructurar o enviar el correo electrónico: " + e.getMessage());
        }
    }
}