package com.api.biblioteca.dto;

public record LibroRequest(
        String codigo,
        String titulo,
        String autor) {
}
