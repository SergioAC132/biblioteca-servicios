package com.api.biblioteca.services;

import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.models.Pago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de Utilidad: Documento.
 * Operación candidata: generarPDF (simulada: devuelve un String, no arma un PDF real).
 */
@Service
public class DocumentoService {

    private static final Logger log = LoggerFactory.getLogger(DocumentoService.class);

    public String generarPDF(Estudiante estudiante, Pago pago) {
        String documento = "Documento generado: constancia de estudios de "
                + estudiante.getNombre() + " (" + estudiante.getMatricula() + "), folio de pago " + pago.getId();
        log.info("[Documento] {}", documento);
        return documento;
    }
}