package com.api.biblioteca.services;

import com.api.biblioteca.dto.PagoRequest;
import com.api.biblioteca.models.Estudiante;
import com.api.biblioteca.models.Pago;
import com.api.biblioteca.repositories.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository repositorio;
    private final EstudianteService estudianteService;

    public PagoService(PagoRepository repositorio, EstudianteService estudianteService) {
        this.repositorio = repositorio;
        this.estudianteService = estudianteService;
    }

    public List<Pago> listar() {
        return repositorio.findAll();
    }

    public Pago consultarPago(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ServicioException(CodigoError.PAGO_NO_ENCONTRADO));
    }

    @Transactional
    public Pago crear(PagoRequest solicitud) {
        Estudiante estudiante = estudianteService.consultarEstudiante(
                Validaciones.texto(solicitud.matricula(), "matricula", 9));
        String concepto = Validaciones.texto(solicitud.concepto(), "concepto", 100);
        return procesarPago(estudiante.getMatricula(), concepto, solicitud.monto());
    }

    @Transactional
    public Pago actualizar(Long id, PagoRequest solicitud) {
        Pago pago = consultarPago(id);
        Estudiante estudiante = estudianteService.consultarEstudiante(
                Validaciones.texto(solicitud.matricula(), "matricula", 9));
        String concepto = Validaciones.texto(solicitud.concepto(), "concepto", 100);
        if (solicitud.monto() == null || solicitud.monto().signum() <= 0) {
            throw new ServicioException(CodigoError.DATOS_INVALIDOS, "El monto debe ser mayor a 0");
        }
        String estado = Validaciones.textoOpcional(solicitud.estado(), "estado", 20);
        pago.setMatricula(estudiante.getMatricula());
        pago.setConcepto(concepto);
        pago.setMonto(solicitud.monto());
        if (estado != null) {
            pago.setEstado(estado.toUpperCase());
        }
        return repositorio.save(pago);
    }

    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(consultarPago(id));
    }

    @Transactional
    public Pago procesarPago(String matricula, String concepto, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new ServicioException(CodigoError.PAGO_RECHAZADO);
        }
        Pago pago = new Pago(matricula, concepto, monto, LocalDateTime.now(), "APROBADO");
        Pago guardado = repositorio.save(pago);
        log.info("[Pago] Pago {} aprobado para {} por ${}", guardado.getId(), matricula, monto);
        return guardado;
    }
}
