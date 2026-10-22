package com.api.biblioteca.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** edo_lib: 'D' = disponible, 'P' = prestado. */
@Converter
public class EstadoCatalogoConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean disponible) {
        return Boolean.TRUE.equals(disponible) ? "D" : "P";
    }

    @Override
    public Boolean convertToEntityAttribute(String valor) {
        return "D".equalsIgnoreCase(valor);
    }
}