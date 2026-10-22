package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.ConstanciaResponse;
import com.api.biblioteca.services.TramiteConstanciaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/constancias")
public class TramiteConstanciaController {

    private final TramiteConstanciaService servicio;

    public TramiteConstanciaController(TramiteConstanciaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/{matricula}")
    @ResponseStatus(HttpStatus.CREATED)
    public ConstanciaResponse solicitarConstancia(@PathVariable String matricula) {
        return servicio.solicitarConstancia(matricula);
    }
}
