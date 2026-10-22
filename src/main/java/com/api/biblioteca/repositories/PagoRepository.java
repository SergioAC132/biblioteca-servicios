package com.api.biblioteca.repositories;
 
import com.api.biblioteca.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface PagoRepository extends JpaRepository<Pago, Long> {
}
 