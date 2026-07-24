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
            helper.setSubject("💪 Tu nueva rutina [" + rutina.getNombreRutina() + "] está lista");
            
            String nombreEscapado = escapeHtml(nombreSocio);
            String rutinaEscapada = escapeHtml(rutina.getNombreRutina());
            int totalEjercicios = rutina.getEjercicios() == null ? 0 : rutina.getEjercicios().size();

            // 1. Construir las filas de la tabla de ejercicios dinámicamente
            StringBuilder filasEjercicios = new StringBuilder();
            int index = 0;
            if (rutina.getEjercicios() != null) {
                for (EjercicioDto ej : rutina.getEjercicios()) {
                    String filaBg = (index % 2 == 0) ? "#0f172a" : "#111827";
                    filasEjercicios.append("<tr style='background-color: ").append(filaBg).append("; border-bottom: 1px solid #1f2937;'>")
                        .append("<td style='padding: 14px 16px; text-align: left; color: #e5e7eb; font-weight: 600;'>").append(escapeHtml(ej.getNombre())).append("</td>")
                        .append("<td style='padding: 14px 16px; text-align: center; color: #f8fafc; font-weight: 700;'>").append(ej.getSeries()).append("</td>")
                        .append("<td style='padding: 14px 16px; text-align: center; color: #f8fafc;'>").append(escapeHtml(ej.getRepeticiones())).append("</td>")
                        .append("</tr>");
                    index++;
                }
            }

            // 2. Armar la plantilla HTML completa con estilos inline (amigable para clientes de correo)
            String contenidoHtml = """
                    <!DOCTYPE html>
                    <html lang="es">
                    <body style="margin:0; padding:0; background-color:#0b1220; font-family:Arial, Helvetica, sans-serif; color:#e5e7eb;">
                      <div style="display:none; max-height:0; overflow:hidden; opacity:0;">
                        Tu rutina %s ya está lista.
                      </div>
                      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:linear-gradient(180deg,#0b1220 0%%,#111827 100%%); padding:32px 16px;">
                        <tr>
                          <td align="center">
                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:680px; width:100%%;">
                              <tr>
                                <td style="padding:0 8px 18px 8px; text-align:center;">
                                  <div style="display:inline-block; width:72px; height:72px; line-height:72px; border-radius:22px; background:linear-gradient(135deg,#fce300 0%%,#f4c900 100%%); color:#111827; font-size:28px; font-weight:900; box-shadow:0 14px 30px rgba(0,0,0,0.28);">
                                    GF
                                  </div>
                                  <div style="margin-top:12px; display:inline-block; padding:10px 16px; border-radius:999px; background:rgba(252,227,0,0.12); color:#fce300; font-size:12px; font-weight:700; letter-spacing:1px; text-transform:uppercase; border:1px solid rgba(252,227,0,0.18);">
                                    Gym Flex • Rutina lista
                                  </div>
                                </td>
                              </tr>
                              <tr>
                                <td style="background:#0f172a; border:1px solid rgba(255,255,255,0.08); border-radius:24px; overflow:hidden; box-shadow:0 20px 50px rgba(0,0,0,0.28);">
                                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                    <tr>
                                      <td style="padding:36px 36px 18px 36px; background:linear-gradient(135deg,#111111 0%%,#1f2937 100%%); color:#f8fafc; border-bottom:1px solid rgba(255,255,255,0.06);">
                                        <div style="font-size:13px; font-weight:700; text-transform:uppercase; letter-spacing:1px; opacity:0.85;">Nueva rutina de entrenamiento</div>
                                        <div style="font-size:28px; line-height:1.2; font-weight:800; margin-top:8px;">Hola, %s</div>
                                        <div style="margin-top:10px; font-size:15px; line-height:1.6; max-width:520px;">
                                          Tu profesor preparó una rutina para que la sigas desde hoy.
                                        </div>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td style="padding:28px 36px 10px 36px;">
                                        <div style="font-size:13px; text-transform:uppercase; letter-spacing:1px; color:#94a3b8; font-weight:700; margin-bottom:10px;">Rutina</div>
                                        <div style="display:inline-block; padding:12px 16px; border-radius:16px; background:#111827; border:1px solid rgba(255,255,255,0.08); color:#f8fafc; font-size:18px; font-weight:700;">
                                          %s
                                        </div>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td style="padding:8px 36px 0 36px;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                          <tr>
                                            <td style="padding:0 8px 0 0;">
                                              <div style="background:rgba(252,227,0,0.08); border:1px solid rgba(252,227,0,0.18); border-radius:18px; padding:14px 16px; text-align:center;">
                                                <div style="font-size:12px; color:#cbd5e1; text-transform:uppercase; letter-spacing:1px; font-weight:700;">Ejercicios</div>
                                                <div style="font-size:24px; color:#fce300; font-weight:800; margin-top:4px;">%d</div>
                                              </div>
                                            </td>
                                            <td style="padding:0 0 0 8px;">
                                              <div style="background:rgba(59,130,246,0.08); border:1px solid rgba(59,130,246,0.18); border-radius:18px; padding:14px 16px; text-align:center;">
                                                <div style="font-size:12px; color:#cbd5e1; text-transform:uppercase; letter-spacing:1px; font-weight:700;">Modo</div>
                                                <div style="font-size:16px; color:#93c5fd; font-weight:800; margin-top:4px;">Entrená duro</div>
                                              </div>
                                            </td>
                                          </tr>
                                        </table>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td style="padding:18px 36px 0 36px;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="border-collapse:separate; border-spacing:0; border-radius:18px; overflow:hidden; border:1px solid rgba(255,255,255,0.08);">
                                          <tr style="background:#1d4ed8; color:#ffffff;">
                                            <th style="padding:14px 16px; text-align:left; font-size:13px; text-transform:uppercase; letter-spacing:0.8px;">Ejercicio</th>
                                            <th style="padding:14px 16px; text-align:center; font-size:13px; text-transform:uppercase; letter-spacing:0.8px;">Series</th>
                                            <th style="padding:14px 16px; text-align:center; font-size:13px; text-transform:uppercase; letter-spacing:0.8px;">Reps</th>
                                          </tr>
                                          %s
                                        </table>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td style="padding:28px 36px 36px 36px;">
                                        <div style="padding:16px 18px; border-radius:16px; background:rgba(252,227,0,0.08); border:1px solid rgba(252,227,0,0.16); color:#e5e7eb; font-size:14px; line-height:1.6;">
                                          Guardá este correo para tener tu rutina siempre a mano. Si tenés dudas, consultá con tu profesor.
                                        </div>
                                        <div style="font-size:12px; color:#94a3b8; text-align:center; margin-top:22px; line-height:1.6;">
                                          Este es un correo automático enviado por el sistema de gestión de Gym Flex.
                                        </div>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </body>
                    </html>
                    """
                    .formatted(rutinaEscapada, nombreEscapado, rutinaEscapada, totalEjercicios, filasEjercicios);

            helper.setText(contenidoHtml, true);
            mailSender.send(mimeMessage);
            System.out.println("📧 Correo con rutina enviado con éxito a: " + emailSocio);

        } catch (Exception e) {
            System.err.println("❌ Error al estructurar o enviar el correo electrónico: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al estructurar o enviar el correo electrónico", e);
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}