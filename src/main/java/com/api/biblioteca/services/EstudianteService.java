package com.api.biblioteca.services;

import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.repositories.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository repositorio;

    public EstudianteService(EstudianteRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Estudiante> listar() {
        return repositorio.findAll();
    }

    public Estudiante consultarEstudiante(String matricula) {
        String canonica = EsquemaCanonico.matricula(matricula);
        return repositorio.findById(canonica)
                .orElseThrow(() -> new ServicioException(CodigoError.ESTUDIANTE_NO_ENCONTRADO));
    }

    public Estudiante verificarInscripcion(String matricula) {
        Estudiante estudiante = consultarEstudiante(matricula);
        if (!estudiante.isActivo()) {
            throw new ServicioException(CodigoError.ESTUDIANTE_NO_INSCRITO);
        }
        return estudiante;
    }
}