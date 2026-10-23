package com.api.biblioteca.dto;

import java.time.LocalDate;

public record DevolucionRequest(
        String matricula,
        String codigoLibro,
        LocalDate fechaDevolucion,
        Long folioPrestamo) {
}
