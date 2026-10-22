package com.api.biblioteca.services;

import org.springframework.http.HttpStatus;

public enum CodigoError {

    ESTUDIANTE_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Estudiante no encontrado"),
    LIBRO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Libro no encontrado"),
    LIBRO_NO_DISPONIBLE(HttpStatus.CONFLICT, "El libro no está disponible"),
    ESTUDIANTE_NO_INSCRITO(HttpStatus.CONFLICT, "El estudiante no está inscrito"),
    PAGO_RECHAZADO(HttpStatus.PAYMENT_REQUIRED, "El pago fue rechazado"),
    DATOS_DEVOLUCION_INVALIDOS(HttpStatus.BAD_REQUEST, "La matrícula y el código del libro son obligatorios"),
    PRESTAMO_ACTIVO_NO_ENCONTRADO(HttpStatus.CONFLICT, "No existe un préstamo activo de ese libro para el estudiante");

    private final HttpStatus estado;
    private final String mensaje;

    CodigoError(HttpStatus estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public HttpStatus getEstado() { return estado; }

    public String getMensaje() { return mensaje; }
}