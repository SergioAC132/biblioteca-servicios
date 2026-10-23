package com.api.biblioteca.controllers;

import com.api.biblioteca.dto.EstudianteRequest;
import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.services.EstudianteService;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estudiante crear(@RequestBody EstudianteRequest solicitud) {
        return servicio.crear(solicitud);
    }

    @PutMapping("/{matricula}")
    public Estudiante actualizar(@PathVariable String matricula, @RequestBody EstudianteRequest solicitud) {
        return servicio.actualizar(matricula, solicitud);
    }

    @DeleteMapping("/{matricula}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String matricula) {
        servicio.eliminar(matricula);
    }
}
