package com.api.biblioteca.services;

public class ServicioException extends RuntimeException {

    private final CodigoError codigo;

    public ServicioException(CodigoError codigo) {
        super(codigo.getMensaje());
        this.codigo = codigo;
    }

    public CodigoError getCodigo() { return codigo; }
}