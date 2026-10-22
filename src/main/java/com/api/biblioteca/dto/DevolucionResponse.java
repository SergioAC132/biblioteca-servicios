package com.api.biblioteca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DevolucionResponse(
        Long idPrestamo,
        String matricula,
        String codigoLibro,
        String titulo,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        int diasRetraso,
        BigDecimal multa,
        String mensaje) {
}
