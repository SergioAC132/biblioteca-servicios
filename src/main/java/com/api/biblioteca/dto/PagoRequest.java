package com.api.biblioteca.dto;

import java.math.BigDecimal;

public record PagoRequest(
        String matricula,
        String concepto,
        BigDecimal monto,
        String estado) {
}
