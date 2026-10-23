package com.api.biblioteca.services;

import org.springframework.http.HttpStatus;

public enum CodigoError {

    ESTUDIANTE_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Estudiante no encontrado"),
    LIBRO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Libro no encontrado"),
    LIBRO_NO_DISPONIBLE(HttpStatus.CONFLICT, "El libro no está disponible"),
    ESTUDIANTE_NO_INSCRITO(HttpStatus.CONFLICT, "El estudiante no está inscrito"),
    PAGO_RECHAZADO(HttpStatus.PAYMENT_REQUIRED, "El pago fue rechazado"),
    DATOS_DEVOLUCION_INVALIDOS(HttpStatus.BAD_REQUEST, "La matrícula, el código del libro, la fecha de devolución y el folio del préstamo son obligatorios"),
    LIBRO_YA_DEVUELTO(HttpStatus.CONFLICT, "El libro de este préstamo ya fue devuelto"),
    DATOS_INVALIDOS(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos"),
    JSON_INVALIDO(HttpStatus.BAD_REQUEST, "El cuerpo de la petición falta o no es un JSON válido"),
    ESTUDIANTE_YA_EXISTE(HttpStatus.CONFLICT, "Ya existe un estudiante con esa matrícula"),
    ESTUDIANTE_CON_REGISTROS(HttpStatus.CONFLICT, "No se puede eliminar el estudiante porque tiene préstamos o notificaciones registrados"),
    LIBRO_YA_EXISTE(HttpStatus.CONFLICT, "Ya existe un libro con ese código"),
    LIBRO_CON_PRESTAMOS(HttpStatus.CONFLICT, "No se puede eliminar el libro porque tiene préstamos registrados"),
    PRESTAMO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Préstamo no encontrado"),
    PRESTAMO_YA_DEVUELTO(HttpStatus.CONFLICT, "No se puede modificar un préstamo que ya fue devuelto"),
    PAGO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "Pago no encontrado"),
    NOTIFICACION_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "Notificación no encontrada");

    private final HttpStatus estado;
    private final String mensaje;

    CodigoError(HttpStatus estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public HttpStatus getEstado() { return estado; }

    public String getMensaje() { return mensaje; }
}