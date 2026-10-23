package com.api.biblioteca.repositories;

import com.api.biblioteca.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    boolean existsByEstudianteMatricula(String matricula);
}
