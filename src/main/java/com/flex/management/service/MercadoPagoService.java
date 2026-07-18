package com.flex.management.service;
import  java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.flex.management.entity.Socio;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MercadoPagoService {
    @Value("${app.base-url}")
    private String baseUrl;

    private final SocioService socioService;
    private final PagoService pagoService;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    // Se ejecuta automáticamente al levantar Spring Boot para inyectar el token
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String crearPreferenciaPago(String dniSocio, Double monto) {
    try {
        Socio socio = socioService.buscarPorDni(dniSocio);

        // 1. Configurar el ítem a cobrar
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title("Cuota Gimnasio - " + socio.getNombre() + " " + socio.getApellido())
                .quantity(1)
                .unitPrice(new BigDecimal(monto.toString()))
                .currencyId("ARS") 
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        // ==========================================
        // 🔥 EL ARREGLO MAGICO: Limpiar la doble barra
        // Si baseUrl termina en "/", se lo quitamos.
        // ==========================================
        String urlLimpia = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        // 2. Configurar URLs de retorno usando urlLimpia
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(urlLimpia + "/pago-exitoso") 
                .pending(urlLimpia + "/pago-pendiente")
                .failure(urlLimpia + "/pago-fallido")
                .build();

        // 3. Crear la petición de la preferencia
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls) 
                .autoReturn("approved") 
                .externalReference(dniSocio)
                .notificationUrl(urlLimpia + "/api/pagos/webhook")
                .build();

        // 4. Comunicarse con la API de Mercado Pago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // 5. Devolver el link de pago (init_point)
        return preference.getInitPoint();

    } catch (MPApiException apiException) {
        System.err.println("❌ ERROR DETALLADO DE MERCADO PAGO:");
        System.err.println("Status HTTP: " + apiException.getApiResponse().getStatusCode());
        System.err.println("Cuerpo del error: " + apiException.getApiResponse().getContent());
        throw new RuntimeException("Error de MP: " + apiException.getMessage());
        
    } catch (Exception e) {
        throw new RuntimeException("Error interno: " + e.getMessage());
    }
}
   public void verificarYAprobarPago(Long paymentId) {
        try {
            // 1. Instanciamos el cliente de pagos de Mercado Pago
            PaymentClient client = new PaymentClient();
            
            // 2. Consultamos la información real del pago en sus servidores
            Payment payment = client.get(paymentId);

            // 3. Verificamos si efectivamente está aprobado
            if ("approved".equals(payment.getStatus())) {
               String dniSocio = payment.getExternalReference();
                Double monto = payment.getTransactionAmount().doubleValue();

                // Le pasamos el paymentId como tercer parámetro
                pagoService.procesarPagoAprobadoMercadoPago(dniSocio, monto, paymentId);
                
                System.out.println("✅ Pago de Mercado Pago [" + paymentId + "] procesado y cuota extendida para el DNI: " + dniSocio);
            } else {
                System.out.println("⏳ Notificación procesada. El pago [" + paymentId + "] está en estado: " + payment.getStatus());
            }

        } catch (DataIntegrityViolationException e) {
            // ATRAPAMOS LA CONDICIÓN DE CARRERA AQUÍ
            // Este catch específico frena el error rojo de la consola cuando PostgreSQL bloquea el duplicado
            System.out.println("🛡️ [DB] Webhook simultáneo ignorado. El pago [" + paymentId + "] ya fue registrado por otro hilo.");
            
        } catch (Exception e) {
            // Cualquier otro error real cae aquí
            System.err.println("❌ Error al consultar el pago en Mercado Pago: " + e.getMessage());
        }
    }
}
