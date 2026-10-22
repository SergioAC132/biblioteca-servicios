package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.DevolucionRequest;
import com.api.biblioteca.dto.DevolucionResponse;
import com.api.biblioteca.services.PrestamoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService servicio;

    public PrestamoController(PrestamoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/devoluciones")
    public DevolucionResponse devolverLibro(@RequestBody DevolucionRequest solicitud) {
        return servicio.devolverLibro(solicitud);
    }
}
