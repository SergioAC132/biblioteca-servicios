package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.NotificacionRequest;
import com.api.biblioteca.models.Notificacion;
import com.api.biblioteca.services.NotificacionService;
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
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    private final NotificacionService servicio;

    public NotificacionController(NotificacionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Notificacion> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Notificacion consultarNotificacion(@PathVariable Long id) {
        return servicio.consultarNotificacion(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notificacion crear(@RequestBody NotificacionRequest solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{id}")
    public Notificacion actualizar(@PathVariable Long id, @RequestBody NotificacionRequest solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
