package com.api.biblioteca.dto;

import java.time.LocalDate;

public record PrestamoRequest(
        String matricula,
        String codigoLibro,
        LocalDate fechaPrestamo) {
}
