package com.api.biblioteca.controllers;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.biblioteca.services.ServicioException;

import java.util.Map;
 
@RestControllerAdvice
public class ManejadorErrores {
 
    @ExceptionHandler(ServicioException.class)
    public ResponseEntity<Map<String, String>> manejar(ServicioException e) {
        return ResponseEntity.status(e.getCodigo().getEstado())
                .body(Map.of("codigo", e.getCodigo().name(), "mensaje", e.getMessage()));
    }
}
 