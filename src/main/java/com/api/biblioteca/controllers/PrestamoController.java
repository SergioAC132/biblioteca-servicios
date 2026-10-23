package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.DevolucionRequest;
import com.api.biblioteca.dto.DevolucionResponse;
import com.api.biblioteca.dto.PrestamoRequest;
import com.api.biblioteca.models.Prestamo;
import com.api.biblioteca.services.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService servicio;

    public PrestamoController(PrestamoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Prestamo> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Prestamo consultarPrestamo(@PathVariable Long id) {
        return servicio.consultarPrestamo(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestamo crear(@RequestBody PrestamoRequest solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public Prestamo actualizar(@PathVariable Long id, @RequestBody PrestamoRequest solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }

    @PostMapping("/devoluciones")
    public DevolucionResponse devolverLibro(@RequestBody DevolucionRequest solicitud) {
        return servicio.devolverLibro(solicitud);
    }
}
