package com.api.biblioteca.dto;

public record ConstanciaResponse(
        String matricula,
        Long folioPago,
        String correoDestino,
        String mensaje) {
}