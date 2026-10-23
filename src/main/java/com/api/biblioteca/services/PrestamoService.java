package com.api.biblioteca.services;

import com.api.biblioteca.dto.DevolucionRequest;
import com.api.biblioteca.dto.DevolucionResponse;
import com.api.biblioteca.dto.PrestamoRequest;
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

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository repositorio;
    private final EstudianteService estudianteService;
    private final LibroService libroService;
    private final ApplicationEventPublisher eventos;
    private final int diasPlazo;

    public PrestamoService(PrestamoRepository repositorio,
                           EstudianteService estudianteService,
                           LibroService libroService,
                           ApplicationEventPublisher eventos,
                           @Value("${prestamo.dias-plazo:7}") int diasPlazo) {
        this.repositorio = repositorio;
        this.estudianteService = estudianteService;
        this.libroService = libroService;
        this.eventos = eventos;
        this.diasPlazo = diasPlazo;
    }

    public List<Prestamo> listar() {
        return repositorio.findAll();
    }

    public Prestamo consultarPrestamo(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ServicioException(CodigoError.PRESTAMO_NO_ENCONTRADO));
    }

    @Transactional
    public Prestamo crear(PrestamoRequest solicitud) {
        Estudiante estudiante = estudianteService.verificarInscripcion(
                Validaciones.texto(solicitud.matricula(), "matricula", 9));
        String codigo = Validaciones.texto(solicitud.codigoLibro(), "codigoLibro", 20);
        LocalDate fechaPrestamo = validarFechaPrestamo(solicitud.fechaPrestamo(), LocalDate.now());
        Libro libro = libroService.marcarPrestado(codigo);

        Prestamo prestamo = new Prestamo();
        prestamo.setEstudiante(estudiante);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(fechaPrestamo);
        return repositorio.save(prestamo);
    }

    @Transactional
    public Prestamo actualizar(Long id, PrestamoRequest solicitud) {
        Prestamo prestamo = consultarPrestamo(id);
        if (prestamo.getFechaDevolucion() != null) {
            throw new ServicioException(CodigoError.PRESTAMO_YA_DEVUELTO);
        }
        String matricula = EsquemaCanonico.matricula(Validaciones.texto(solicitud.matricula(), "matricula", 9));
        String codigo = EsquemaCanonico.codigoLibro(Validaciones.texto(solicitud.codigoLibro(), "codigoLibro", 20));
        LocalDate fechaPrestamo = validarFechaPrestamo(solicitud.fechaPrestamo(), prestamo.getFechaPrestamo());

        if (!matricula.equals(prestamo.getEstudiante().getMatricula())) {
            prestamo.setEstudiante(estudianteService.verificarInscripcion(matricula));
        }
        if (!codigo.equals(prestamo.getLibro().getCodigo())) {
            Libro nuevo = libroService.marcarPrestado(codigo);
            libroService.marcarDisponible(prestamo.getLibro().getCodigo());
            prestamo.setLibro(nuevo);
        }
        prestamo.setFechaPrestamo(fechaPrestamo);
        return repositorio.save(prestamo);
    }

    @Transactional
    public void eliminar(Long id) {
        Prestamo prestamo = consultarPrestamo(id);
        if (prestamo.getFechaDevolucion() == null) {
            libroService.marcarDisponible(prestamo.getLibro().getCodigo());
        }
        repositorio.delete(prestamo);
    }

    @Transactional
    public DevolucionResponse devolverLibro(DevolucionRequest solicitud) {
        if (solicitud == null || estaVacio(solicitud.matricula()) || estaVacio(solicitud.codigoLibro())
                || solicitud.fechaDevolucion() == null || solicitud.folioPrestamo() == null) {
            throw new ServicioException(CodigoError.DATOS_DEVOLUCION_INVALIDOS);
        }
        String matricula = EsquemaCanonico.matricula(solicitud.matricula());
        String codigoLibro = EsquemaCanonico.codigoLibro(solicitud.codigoLibro());
        if (matricula.length() != 9) {
            throw new ServicioException(CodigoError.DATOS_DEVOLUCION_INVALIDOS,
                    "La matrícula debe tener 9 caracteres");
        }

        Prestamo prestamo = consultarPrestamo(solicitud.folioPrestamo());
        if (!matricula.equals(prestamo.getEstudiante().getMatricula())
                || !codigoLibro.equals(prestamo.getLibro().getCodigo())) {
            throw new ServicioException(CodigoError.PRESTAMO_NO_ENCONTRADO,
                    "El estudiante no tiene un préstamo de ese libro con el folio indicado");
        }
        if (prestamo.getFechaDevolucion() != null) {
            throw new ServicioException(CodigoError.LIBRO_YA_DEVUELTO);
        }

        LocalDate fechaDevolucion = solicitud.fechaDevolucion();
        if (fechaDevolucion.isAfter(LocalDate.now())) {
            throw new ServicioException(CodigoError.DATOS_DEVOLUCION_INVALIDOS,
                    "La fecha de devolución no puede ser futura");
        }
        if (prestamo.getFechaPrestamo() != null && fechaDevolucion.isBefore(prestamo.getFechaPrestamo())) {
            throw new ServicioException(CodigoError.DATOS_DEVOLUCION_INVALIDOS,
                    "La fecha de devolución no puede ser anterior a la fecha de préstamo");
        }

        prestamo.setFechaDevolucion(fechaDevolucion);
        repositorio.save(prestamo);

        eventos.publishEvent(new LibroDevuelto(codigoLibro));

        boolean conRetraso = prestamo.getFechaPrestamo() != null
                && fechaDevolucion.isAfter(prestamo.getFechaPrestamo().plusDays(diasPlazo));
        String estatus = conRetraso ? "DEVUELTO_CON_RETRASO" : "DEVUELTO";

        log.info("[Préstamo] Folio {}: {} devolvió {} el {} ({})",
                prestamo.getId(), matricula, codigoLibro, fechaDevolucion, estatus);

        return new DevolucionResponse(prestamo.getId(), estatus, fechaDevolucion);
    }

    private static LocalDate validarFechaPrestamo(LocalDate fecha, LocalDate porDefecto) {
        if (fecha == null) {
            return porDefecto;
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new ServicioException(CodigoError.DATOS_INVALIDOS, "La fecha de préstamo no puede ser futura");
        }
        return fecha;
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
