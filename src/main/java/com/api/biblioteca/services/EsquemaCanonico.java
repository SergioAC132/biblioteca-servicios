package com.api.biblioteca.services;

/**
 * Normaliza los identificadores que llegan por la URL a su forma canónica
 * (sin espacios y en mayúsculas) antes de buscarlos en la BD.
 */
public final class EsquemaCanonico {

    private EsquemaCanonico() {
    }

    public static String matricula(String matricula) {
        return normalizar(matricula);
    }

    public static String codigoLibro(String codigo) {
        return normalizar(codigo);
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase();
    }
}