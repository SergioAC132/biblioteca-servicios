package com.api.biblioteca.repositories;

import com.api.biblioteca.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    boolean existsByEstudianteMatricula(String matricula);

    boolean existsByLibroCodigo(String codigo);
}
