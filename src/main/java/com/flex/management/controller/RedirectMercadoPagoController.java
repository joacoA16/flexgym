package com.flex.management.controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectMercadoPagoController {

    private String renderPagoPage(String title, String iconClass, String accentClass, String heading, String message) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                    <style>
                        :root {
                            --bg-1: #eef2ff;
                            --bg-2: #f8fafc;
                            --text: #0f172a;
                            --muted: #64748b;
                        }

                        body {
                            min-height: 100vh;
                            margin: 0;
                            background: radial-gradient(circle at top, #ffffff 0%%, var(--bg-1) 38%%, var(--bg-2) 100%%);
                            color: var(--text);
                            font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
                        }

                        .page-wrap {
                            min-height: 100vh;
                        }

                        .payment-card {
                            max-width: 560px;
                            width: 100%%;
                            border: 0;
                            border-radius: 28px;
                            background: rgba(255, 255, 255, 0.92);
                            box-shadow: 0 20px 60px rgba(15, 23, 42, 0.12);
                            backdrop-filter: blur(10px);
                        }

                        .icon-badge {
                            width: 92px;
                            height: 92px;
                            border-radius: 24px;
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 3rem;
                            line-height: 1;
                            background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(99, 102, 241, 0.18));
                            border: 1px solid rgba(148, 163, 184, 0.22);
                        }

                        .icon-badge.%s {
                            background: linear-gradient(135deg, rgba(16, 185, 129, 0.14), rgba(34, 197, 94, 0.18));
                        }

                        .icon-badge.warning {
                            background: linear-gradient(135deg, rgba(245, 158, 11, 0.14), rgba(251, 191, 36, 0.18));
                        }

                        .icon-badge.danger {
                            background: linear-gradient(135deg, rgba(239, 68, 68, 0.14), rgba(248, 113, 113, 0.18));
                        }

                        .eyebrow {
                            display: inline-flex;
                            align-items: center;
                            gap: .4rem;
                            padding: .35rem .75rem;
                            border-radius: 999px;
                            font-size: .82rem;
                            font-weight: 600;
                            color: #475569;
                            background: rgba(148, 163, 184, 0.12);
                        }

                        .title {
                            font-size: clamp(1.8rem, 4vw, 2.4rem);
                            font-weight: 800;
                            letter-spacing: -0.03em;
                        }

                        .lead-text {
                            color: var(--muted);
                            font-size: 1.05rem;
                            line-height: 1.6;
                        }
                    </style>
                </head>
                <body>
                    <main class="page-wrap d-flex align-items-center justify-content-center p-3 p-md-4">
                        <section class="payment-card p-4 p-md-5 text-center">
                            <div class="eyebrow mb-3">
                                <span>Mercado Pago</span>
                                <span class="text-%s">●</span>
                            </div>

                            <div class="icon-badge %s mx-auto mb-4">%s</div>

                            <h1 class="title mb-3">%s</h1>
                            <p class="lead-text mb-4">%s</p>

                            <div class="alert alert-light border-0 shadow-sm mb-0" role="alert">
                                Esta ventana se puede cerrar cuando quieras.
                            </div>
                        </section>
                    </main>
                </body>
                </html>
                """
                .formatted(title, accentClass, accentClass, iconClass, heading, message);
    }

    @GetMapping(value = "/pago-exitoso", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoExitoso() {
        return renderPagoPage(
                "Pago Exitoso",
                "✅",
                "success",
                "¡Pago aprobado!",
                "Tu cuota del gimnasio ha sido actualizada exitosamente."
        );
    }

    @GetMapping(value = "/pago-pendiente", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoPendiente() {
        return renderPagoPage(
                "Pago Pendiente",
                "⏳",
                "warning",
                "Pago pendiente",
                "Estamos esperando la confirmación de Mercado Pago."
        );
    }

    @GetMapping(value = "/pago-fallido", produces = MediaType.TEXT_HTML_VALUE)
    public String pagoFallido() {
        return renderPagoPage(
                "Pago Fallido",
                "❌",
                "danger",
                "Pago rechazado",
                "Hubo un problema al procesar tu tarjeta."
        );
    }
}