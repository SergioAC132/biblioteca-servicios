package com.api.biblioteca.services;

import org.springframework.http.HttpStatus;

public enum CodigoError {

    ESTUDIANTE_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Estudiante no encontrado"),
    LIBRO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Libro no encontrado"),
    LIBRO_NO_DISPONIBLE(HttpStatus.CONFLICT, "El libro no está disponible"),
    ESTUDIANTE_NO_INSCRITO(HttpStatus.CONFLICT, "El estudiante no está inscrito"),
    PAGO_RECHAZADO(HttpStatus.PAYMENT_REQUIRED, "El pago fue rechazado");

    private final HttpStatus estado;
    private final String mensaje;

    CodigoError(HttpStatus estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public HttpStatus getEstado() { return estado; }

    public String getMensaje() { return mensaje; }
}