package com.api.biblioteca.services;

import com.api.biblioteca.dto.NotificacionRequest;
import com.api.biblioteca.models.Notificacion;
import com.api.biblioteca.repositories.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Utilidad: Notificación.
 * Operación candidata: enviarCorreo (simulada: solo registra en el log, no manda correo real).
 */
@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository repositorio;
    private final EstudianteService estudianteService;

    public NotificacionService(NotificacionRepository repositorio, EstudianteService estudianteService) {
        this.repositorio = repositorio;
        this.estudianteService = estudianteService;
    }

    public void enviarCorreo(String destinatario, String asunto, String documento) {
        log.info("[Notificación] Correo enviado a {} | Asunto: {} | Adjunto: {}", destinatario, asunto, documento);
    }

    public List<Notificacion> listar() {
        return repositorio.findAll();
    }

    public Notificacion consultarNotificacion(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ServicioException(CodigoError.NOTIFICACION_NO_ENCONTRADA));
    }

    @Transactional
    public Notificacion crear(NotificacionRequest solicitud) {
        Notificacion notificacion = new Notificacion();
        asignarDatos(notificacion, solicitud);
        notificacion.setFecha(LocalDateTime.now());
        return repositorio.save(notificacion);
    }

    @Transactional
    public Notificacion actualizar(Long id, NotificacionRequest solicitud) {
        Notificacion notificacion = consultarNotificacion(id);
        asignarDatos(notificacion, solicitud);
        return repositorio.save(notificacion);
    }

    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(consultarNotificacion(id));
    }

    private void asignarDatos(Notificacion notificacion, NotificacionRequest solicitud) {
        String matricula = Validaciones.texto(solicitud.matricula(), "matricula", 9);
        String mensaje = Validaciones.texto(solicitud.mensaje(), "mensaje", 200);
        notificacion.setEstudiante(estudianteService.consultarEstudiante(matricula));
        notificacion.setMensaje(mensaje);
    }
}
