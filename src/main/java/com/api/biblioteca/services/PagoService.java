package com.api.biblioteca.services;

import com.api.biblioteca.models.Pago;
import com.api.biblioteca.repositories.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository repositorio;

    public PagoService(PagoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public Pago procesarPago(String matricula, String concepto, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new ServicioException(CodigoError.PAGO_RECHAZADO);
        }
        Pago pago = new Pago(matricula, concepto, monto, LocalDateTime.now(), "APROBADO");
        Pago guardado = repositorio.save(pago);
        log.info("[Pago] Pago {} aprobado para {} por ${}", guardado.getId(), matricula, monto);
        return guardado;
    }
}