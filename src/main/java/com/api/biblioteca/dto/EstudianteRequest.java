package com.api.biblioteca.dto;

public record EstudianteRequest(
        String matricula,
        String nombre,
        String correo,
        Boolean activo) {
}
