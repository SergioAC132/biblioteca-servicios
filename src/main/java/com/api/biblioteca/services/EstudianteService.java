package com.api.biblioteca.services;

import com.api.biblioteca.dto.EstudianteRequest;
import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.repositories.EstudianteRepository;
import com.api.biblioteca.repositories.NotificacionRepository;
import com.api.biblioteca.repositories.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstudianteService {

    private final EstudianteRepository repositorio;
    private final PrestamoRepository prestamoRepositorio;
    private final NotificacionRepository notificacionRepositorio;

    public EstudianteService(EstudianteRepository repositorio,
                             PrestamoRepository prestamoRepositorio,
                             NotificacionRepository notificacionRepositorio) {
        this.repositorio = repositorio;
        this.prestamoRepositorio = prestamoRepositorio;
        this.notificacionRepositorio = notificacionRepositorio;
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

    @Transactional
    public Estudiante crear(EstudianteRequest solicitud) {
        String matricula = EsquemaCanonico.matricula(Validaciones.texto(solicitud.matricula(), "matricula", 9));
        String nombre = Validaciones.texto(solicitud.nombre(), "nombre", 100);
        String correo = Validaciones.texto(solicitud.correo(), "correo", 100);
        if (repositorio.existsById(matricula)) {
            throw new ServicioException(CodigoError.ESTUDIANTE_YA_EXISTE);
        }
        boolean activo = solicitud.activo() == null || solicitud.activo();
        return repositorio.save(new Estudiante(matricula, nombre, correo, activo));
    }

    @Transactional
    public Estudiante actualizar(String matricula, EstudianteRequest solicitud) {
        Estudiante estudiante = consultarEstudiante(matricula);
        estudiante.setNombre(Validaciones.texto(solicitud.nombre(), "nombre", 100));
        estudiante.setCorreo(Validaciones.texto(solicitud.correo(), "correo", 100));
        if (solicitud.activo() != null) {
            estudiante.setActivo(solicitud.activo());
        }
        return repositorio.save(estudiante);
    }

    @Transactional
    public void eliminar(String matricula) {
        Estudiante estudiante = consultarEstudiante(matricula);
        if (prestamoRepositorio.existsByEstudianteMatricula(estudiante.getMatricula())
                || notificacionRepositorio.existsByEstudianteMatricula(estudiante.getMatricula())) {
            throw new ServicioException(CodigoError.ESTUDIANTE_CON_REGISTROS);
        }
        repositorio.delete(estudiante);
    }
}
