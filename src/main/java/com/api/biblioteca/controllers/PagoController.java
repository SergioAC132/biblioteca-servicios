package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.PagoRequest;
import com.api.biblioteca.models.Pago;
import com.api.biblioteca.services.PagoService;
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
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoService servicio;

    public PagoController(PagoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Pago> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Pago consultarPago(@PathVariable Long id) {
        return servicio.consultarPago(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pago crear(@RequestBody PagoRequest solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Long id, @RequestBody PagoRequest solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
