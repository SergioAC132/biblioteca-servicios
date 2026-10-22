package com.api.biblioteca.repositories;

import com.api.biblioteca.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Optional<Prestamo> findFirstByEstudianteMatriculaAndLibroCodigoAndFechaDevolucionIsNull(
            String matricula, String codigoLibro);
}
