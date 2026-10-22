package com.api.biblioteca.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de Utilidad: Notificación.
 * Operación candidata: enviarCorreo (simulada: solo registra en el log, no manda correo real).
 */
@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    public void enviarCorreo(String destinatario, String asunto, String documento) {
        log.info("[Notificación] Correo enviado a {} | Asunto: {} | Adjunto: {}", destinatario, asunto, documento);
    }
}