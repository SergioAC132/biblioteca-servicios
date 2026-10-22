package com.api.biblioteca.repositories;

import com.api.biblioteca.models.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
}
