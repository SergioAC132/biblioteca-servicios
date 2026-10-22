package com.api.biblioteca.controllers;

import com.api.biblioteca.models.Libro;
import com.api.biblioteca.services.LibroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
