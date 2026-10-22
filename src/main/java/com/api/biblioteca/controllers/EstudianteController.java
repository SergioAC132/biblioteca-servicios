package com.api.biblioteca.controllers;

import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.services.EstudianteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService servicio;

    public EstudianteController(EstudianteService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Estudiante> listar() {
        return servicio.listar();
    }

    @GetMapping("/{matricula}")
    public Estudiante consultarEstudiante(@PathVariable String matricula) {
        return servicio.consultarEstudiante(matricula);
    }
}
