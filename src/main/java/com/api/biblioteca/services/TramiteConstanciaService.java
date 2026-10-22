package com.api.biblioteca.services;

import com.api.biblioteca.dto.ConstanciaResponse;
import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.models.Pago;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TramiteConstanciaService {

    private static final Logger log = LoggerFactory.getLogger(TramiteConstanciaService.class);
    private static final String CONCEPTO = "Constancia de estudios";

    private final EstudianteService estudianteService;
    private final PagoService pagoService;
    private final DocumentoService documentoService;
    private final NotificacionService notificacionService;
    private final BigDecimal costo;

    public TramiteConstanciaService(EstudianteService estudianteService,
                                    PagoService pagoService,
                                    DocumentoService documentoService,
                                    NotificacionService notificacionService,
                                    @Value("${constancia.costo:50.00}") BigDecimal costo) {
        this.estudianteService = estudianteService;
        this.pagoService = pagoService;
        this.documentoService = documentoService;
        this.notificacionService = notificacionService;
        this.costo = costo;
    }

    @Transactional
    public ConstanciaResponse solicitarConstancia(String matricula) {
        log.info("[Trámite] Solicitud de constancia para {}", matricula);

        Estudiante estudiante = estudianteService.verificarInscripcion(matricula);

        Pago pago = pagoService.procesarPago(estudiante.getMatricula(), CONCEPTO, costo);

        String documento = documentoService.generarPDF(estudiante, pago);

        notificacionService.enviarCorreo(estudiante.getCorreo(), "Tu constancia de estudios", documento);

        return new ConstanciaResponse(
                estudiante.getMatricula(),
                pago.getId(),
                estudiante.getCorreo(),
                "Constancia enviada correctamente");
    }
}