package com.api.biblioteca.services;

import com.api.biblioteca.dto.LibroRequest;
import com.api.biblioteca.models.Libro;
import com.api.biblioteca.models.LibroDevuelto;
import com.api.biblioteca.repositories.LibroRepository;
import com.api.biblioteca.repositories.PrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LibroService {

    private static final Logger log = LoggerFactory.getLogger(LibroService.class);

    private final LibroRepository repositorio;
    private final PrestamoRepository prestamoRepositorio;

    public LibroService(LibroRepository repositorio, PrestamoRepository prestamoRepositorio) {
        this.repositorio = repositorio;
        this.prestamoRepositorio = prestamoRepositorio;
    }

    public List<Libro> listar() {
        return repositorio.findAll();
    }

    public Libro consultarLibro(String codigo) {
        String canonico = EsquemaCanonico.codigoLibro(codigo);
        return repositorio.findById(canonico)
                .orElseThrow(() -> new ServicioException(CodigoError.LIBRO_NO_ENCONTRADO));
    }

    public Libro consultarDisponibilidad(String codigo) {
        return consultarLibro(codigo);
    }

    @Transactional
    public Libro crear(LibroRequest solicitud) {
        String codigo = EsquemaCanonico.codigoLibro(Validaciones.texto(solicitud.codigo(), "codigo", 20));
        String titulo = Validaciones.texto(solicitud.titulo(), "titulo", 150);
        String autor = Validaciones.textoOpcional(solicitud.autor(), "autor", 100);
        if (repositorio.existsById(codigo)) {
            throw new ServicioException(CodigoError.LIBRO_YA_EXISTE);
        }
        return repositorio.save(new Libro(codigo, titulo, autor, true));
    }

    @Transactional
    public Libro actualizar(String codigo, LibroRequest solicitud) {
        Libro libro = consultarLibro(codigo);
        libro.setTitulo(Validaciones.texto(solicitud.titulo(), "titulo", 150));
        libro.setAutor(Validaciones.textoOpcional(solicitud.autor(), "autor", 100));
        return repositorio.save(libro);
    }

    @Transactional
    public void eliminar(String codigo) {
        Libro libro = consultarLibro(codigo);
        if (prestamoRepositorio.existsByLibroCodigo(libro.getCodigo())) {
            throw new ServicioException(CodigoError.LIBRO_CON_PRESTAMOS);
        }
        repositorio.delete(libro);
    }

    @Transactional
    public Libro marcarPrestado(String codigo) {
        Libro libro = consultarLibro(codigo);
        if (!Boolean.TRUE.equals(libro.getDisponible())) {
            throw new ServicioException(CodigoError.LIBRO_NO_DISPONIBLE);
        }
        libro.setDisponible(false);
        return repositorio.save(libro);
    }

    @Transactional
    public Libro marcarDisponible(String codigo) {
        Libro libro = consultarLibro(codigo);
        libro.setDisponible(true);
        return repositorio.save(libro);
    }

    @EventListener
    public void alDevolverseUnLibro(LibroDevuelto evento) {
        marcarDisponible(evento.codigoLibro());
        log.info("[Libro] Evento libroDevuelto recibido: {} vuelve a estar disponible", evento.codigoLibro());
    }
}
