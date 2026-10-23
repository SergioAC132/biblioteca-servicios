package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.LibroRequest;
import com.api.biblioteca.models.Libro;
import com.api.biblioteca.services.LibroService;
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
@RequestMapping("/api/v1/libros")
public class LibroController {

    private final LibroService servicio;

    public LibroController(LibroService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Libro> listar() {
        return servicio.listar();
    }

    @GetMapping("/{codigo}")
    public Libro consultarLibro(@PathVariable String codigo) {
        return servicio.consultarLibro(codigo);
    }

    @GetMapping("/{codigo}/disponibilidad")
    public Libro consultarDisponibilidad(@PathVariable String codigo) {
        return servicio.consultarDisponibilidad(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro crear(@RequestBody LibroRequest solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{codigo}")
    public Libro actualizar(@PathVariable String codigo, @RequestBody LibroRequest solicitud) {
        return servicio.actualizar(codigo, solicitud);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigo) {
        servicio.eliminar(codigo);
    }
}
