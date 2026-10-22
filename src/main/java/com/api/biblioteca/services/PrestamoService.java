package com.api.biblioteca.services;

import com.api.biblioteca.dto.DevolucionRequest;
import com.api.biblioteca.dto.DevolucionResponse;
import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.models.Libro;
import com.api.biblioteca.models.LibroDevuelto;
import com.api.biblioteca.models.Prestamo;
import com.api.biblioteca.repositories.PrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository repositorio;
    private final EstudianteService estudianteService;
    private final LibroService libroService;
    private final ApplicationEventPublisher eventos;
    private final int diasPlazo;
    private final BigDecimal multaPorDia;

    public PrestamoService(PrestamoRepository repositorio,
                           EstudianteService estudianteService,
                           LibroService libroService,
                           ApplicationEventPublisher eventos,
                           @Value("${prestamo.dias-plazo:7}") int diasPlazo,
                           @Value("${prestamo.multa-por-dia:5.00}") BigDecimal multaPorDia) {
        this.repositorio = repositorio;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        this.eventos = eventos;
        this.diasPlazo = diasPlazo;
        this.multaPorDia = multaPorDia;
    }

    @Transactional
    public DevolucionResponse devolverLibro(DevolucionRequest solicitud) {
        if (solicitud == null || estaVacio(solicitud.matricula()) || estaVacio(solicitud.codigoLibro())) {
            throw new ServicioException(CodigoError.DATOS_DEVOLUCION_INVALIDOS);
        }

        Estudiante estudiante = estudianteService.consultarEstudiante(solicitud.matricula());
        Libro libro = libroService.consultarLibro(solicitud.codigoLibro());

        Prestamo prestamo = repositorio
                .findFirstByEstudianteMatriculaAndLibroCodigoAndFechaDevolucionIsNull(
                        estudiante.getMatricula(), libro.getCodigo())
                .orElseThrow(() -> new ServicioException(CodigoError.PRESTAMO_ACTIVO_NO_ENCONTRADO));

        LocalDate hoy = LocalDate.now();
        int diasRetraso = 0;
        if (prestamo.getFechaPrestamo() != null) {
            LocalDate fechaLimite = prestamo.getFechaPrestamo().plusDays(diasPlazo);
            diasRetraso = (int) Math.max(0, ChronoUnit.DAYS.between(fechaLimite, hoy));
        }
        BigDecimal multa = multaPorDia.multiply(BigDecimal.valueOf(diasRetraso));

        prestamo.setFechaDevolucion(hoy);
        repositorio.save(prestamo);

        eventos.publishEvent(new LibroDevuelto(libro.getCodigo()));

        log.info("[Préstamo] {} devolvió {} con {} día(s) de retraso, multa ${}",
                estudiante.getMatricula(), libro.getCodigo(), diasRetraso, multa);

        return new DevolucionResponse(
                prestamo.getId(),
                estudiante.getMatricula(),
                libro.getCodigo(),
                libro.getTitulo(),
                prestamo.getFechaPrestamo(),
                hoy,
                diasRetraso,
                multa,
                diasRetraso > 0 ? "Libro devuelto con retraso" : "Libro devuelto a tiempo");
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
