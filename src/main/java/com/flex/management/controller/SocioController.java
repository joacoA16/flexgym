package com.flex.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flex.management.DTO.RutinaDto;
import com.flex.management.entity.Socio;
import com.flex.management.service.EmailService;
import com.flex.management.service.SocioService;
import com.flex.management.service.WhatsAppService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/socios")
@CrossOrigin(origins = "*") // Permite conectar con el front local más adelante
public class SocioController {

    private final WhatsAppService whatsAppService;
    private final SocioService socioService;
    private final EmailService emailService;

    // --- ENDPOINTS PARA EL PROFESOR (ADMIN) ---

  

@GetMapping
    public ResponseEntity<List<Socio>> obtenerTodosLosSocios() {
        // Asumiendo que tienes un método findAll() en tu socioService o socioRepository
        List<Socio> socios = socioService.obtenerTodos(); 
        return ResponseEntity.ok(socios);
    }

    // Registrar un nuevo socio
    @PostMapping
    public ResponseEntity<Socio> registrarSocio(@RequestBody Socio socio) {
        // Por defecto, al crearlo lo ideal es que inicie activo
        socio.setActivo(true);
        Socio nuevoSocio = socioService.guardarSocio(socio);
        return ResponseEntity.ok(nuevoSocio);
    }

    // Enviar rutina por correo electrónico (Solo simulación por ahora)
 @PostMapping("/{dni}/rutina")
    public ResponseEntity<String> enviarRutina(@PathVariable String dni, @RequestBody RutinaDto rutinaDto) {
        // Buscamos al socio para verificar que existe y obtener su mail y nombre
        Socio socio = socioService.buscarPorDni(dni);
        
        if (socio.getEmail() == null || socio.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("El socio no tiene un correo electrónico registrado.");
        }

        // Despachamos la rutina dinámica
        emailService.enviarRutinaHtml(socio.getEmail(), socio.getNombre(), rutinaDto);
        
        return ResponseEntity.ok("Rutina \"" + rutinaDto.getNombreRutina() + "\" enviada correctamente al correo: " + socio.getEmail());
    }
    

    // --- ENDPOINT PARA EL SOCIO ---

    // Buscar estado de cuota por DNI (Login minimalista del socio)
    @GetMapping("/buscar")
    public ResponseEntity<Socio> buscarPorDni(@RequestParam String dni) {
        Socio socio = socioService.buscarPorDni(dni);
        
        // RECOMENDACIÓN DE SEGURIDAD / PRIVACIDAD:
        // Para evitar que cualquiera husmee datos ajenos clonamos el objeto
        // y limpiamos datos sensibles antes de enviarlo al cliente.
        Socio datosPublicos = new Socio();
        datosPublicos.setDni(socio.getDni());
        datosPublicos.setNombre(socio.getNombre());
        datosPublicos.setApellido(socio.getApellido());
        datosPublicos.setFechaVencimientoCuota(socio.getFechaVencimientoCuota());
        datosPublicos.setActivo(socio.isActivo());
        // NO seteamos email ni teléfono para mitigar riesgos de enumeración
        
        return ResponseEntity.ok(datosPublicos);
    }
    @PostMapping("/{dni}/whatsapp")
    public ResponseEntity<String> enviarRecordatorioWhatsApp(@PathVariable String dni) {
        Socio socio = socioService.buscarPorDni(dni);
        
        if (socio.getTelefono() == null || socio.getTelefono().isEmpty()) {
            return ResponseEntity.badRequest().body("El socio no tiene número de teléfono registrado.");
        }

        // Llamamos al servicio pasando el número del socio y la plantilla de prueba
        whatsAppService.enviarMensajeTemplate(socio.getTelefono(), "hello_world");
        
        return ResponseEntity.ok("Notificación enviada por WhatsApp al número: " + socio.getTelefono());
    }
}
