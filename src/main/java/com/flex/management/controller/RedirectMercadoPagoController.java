package com.flex.management.controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectMercadoPagoController {

    @GetMapping(value = "/pago-exitoso", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoExitoso() {
        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>Pago Exitoso</title><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'></head><body class='bg-light d-flex align-items-center justify-content-center' style='height: 100vh;'><div class='text-center p-5 card shadow-lg' style='max-width: 500px;'><h1 class='text-success display-1'>✅</h1><h2 class='mt-3 text-success'>¡Pago Aprobado!</h2><p class='lead mt-3'>Tu cuota del gimnasio ha sido actualizada exitosamente.</p><p class='text-muted'>Ya puedes cerrar esta pestaña y volver a tu panel del gimnasio.</p></div></body></html>";
    }

    @GetMapping(value = "/pago-pendiente", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoPendiente() {
        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>Pago Pendiente</title><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'></head><body class='bg-light d-flex align-items-center justify-content-center' style='height: 100vh;'><div class='text-center p-5 card shadow-lg' style='max-width: 500px;'><h1 class='text-warning display-1'>⏳</h1><h2 class='mt-3 text-warning'>Pago Pendiente</h2><p class='lead mt-3'>Estamos esperando la confirmación de Mercado Pago.</p><p class='text-muted'>Te avisaremos en cuanto se acredite. Ya puedes cerrar esta pestaña.</p></div></body></html>";
    }

    @GetMapping(value = "/pago-fallido", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoFallido() {
        return "<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>Pago Fallido</title><link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'></head><body class='bg-light d-flex align-items-center justify-content-center' style='height: 100vh;'><div class='text-center p-5 card shadow-lg' style='max-width: 500px;'><h1 class='text-danger display-1'>❌</h1><h2 class='mt-3 text-danger'>Pago Rechazado</h2><p class='lead mt-3'>Hubo un problema al procesar tu tarjeta.</p><p class='text-muted'>Por favor, cierra esta pestaña e intenta nuevamente desde tu panel.</p></div></body></html>";
    }
}