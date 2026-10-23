package com.api.biblioteca.dto;

import java.time.LocalDate;

public record DevolucionResponse(
        Long folioPrestamo,
        String estatus,
        LocalDate fechaDevolucion) {
}
