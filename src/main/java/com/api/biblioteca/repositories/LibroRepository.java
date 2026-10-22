package com.api.biblioteca.repositories;

import com.api.biblioteca.models.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, String> {
}
