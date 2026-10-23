package com.api.biblioteca.services;

public final class Validaciones {

    private Validaciones() {
    }

    public static String texto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new ServicioException(CodigoError.DATOS_INVALIDOS, "El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ServicioException(CodigoError.DATOS_INVALIDOS,
                    "El campo " + campo + " admite máximo " + maximo + " caracteres");
        }
        return limpio;
    }

    public static String textoOpcional(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return texto(valor, campo, maximo);
    }
}
