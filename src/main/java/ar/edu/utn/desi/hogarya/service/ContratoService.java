package ar.edu.utn.desi.hogarya.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.desi.hogarya.model.Contrato;
import ar.edu.utn.desi.hogarya.model.EstadoContrato;
import ar.edu.utn.desi.hogarya.model.EstadoDisponibilidad;
import ar.edu.utn.desi.hogarya.model.HistorialEstadoContrato;
import ar.edu.utn.desi.hogarya.model.HistorialEstadoPropiedad;
import ar.edu.utn.desi.hogarya.model.Propiedad;
import ar.edu.utn.desi.hogarya.repository.ContratoRepository;
import ar.edu.utn.desi.hogarya.repository.HistorialEstadoContratoRepository;
import ar.edu.utn.desi.hogarya.repository.HistorialEstadoPropiedadRepository;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;

@Service
public class ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private HistorialEstadoContratoRepository historialContratoRepository;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private HistorialEstadoPropiedadRepository historialPropiedadRepository;

    public List<Contrato> listarActivos() {
        return contratoRepository.findByEliminadoFalse();
    }

    public Contrato buscarPorId(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));
    }

    public Contrato crear(Contrato contrato) {
        contrato.setEstado(EstadoContrato.BORRADOR);
        contrato.setEliminado(false);
        contrato.setPropietario(contrato.getPropiedad().getPropietario());

        Contrato guardado = contratoRepository.save(contrato);
        registrarHistorial(guardado);
        return guardado;
    }

    public Contrato modificar(Long id, Contrato datosNuevos) {
        Contrato existente = buscarPorId(id);

        if (existente.getEstado() != EstadoContrato.BORRADOR) {
            throw new IllegalStateException("Solo se pueden modificar contratos en estado BORRADOR.");
        }

        existente.setFechaInicio(datosNuevos.getFechaInicio());
        existente.setDuracionMeses(datosNuevos.getDuracionMeses());
        existente.setImporteMensual(datosNuevos.getImporteMensual());
        existente.setDiaVencimientoMensual(datosNuevos.getDiaVencimientoMensual());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setPropiedad(datosNuevos.getPropiedad());
        existente.setInquilino(datosNuevos.getInquilino());
        existente.setPropietario(datosNuevos.getPropiedad().getPropietario());

        return contratoRepository.save(existente);
    }

    public void eliminar(Long id) {
        Contrato contrato = buscarPorId(id);

        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            throw new IllegalStateException("Solo se pueden eliminar contratos en estado BORRADOR.");
        }

        contrato.setEliminado(true);
        contratoRepository.save(contrato);
    }

    public Contrato cambiarEstado(Long id, EstadoContrato nuevoEstado) {
        Contrato contrato = buscarPorId(id);

        validarTransicion(contrato.getEstado(), nuevoEstado);

        if (nuevoEstado == EstadoContrato.ACTIVO) {
            activar(contrato);
        } else {
            liberarPropiedad(contrato);
        }

        contrato.setEstado(nuevoEstado);
        Contrato actualizado = contratoRepository.save(contrato);
        registrarHistorial(actualizado);
        return actualizado;
    }

    private void validarTransicion(EstadoContrato actual, EstadoContrato nuevo) {
        boolean valida = (actual == EstadoContrato.BORRADOR && nuevo == EstadoContrato.ACTIVO)
                || (actual == EstadoContrato.ACTIVO  && nuevo == EstadoContrato.FINALIZADO)
                || (actual == EstadoContrato.ACTIVO  && nuevo == EstadoContrato.RESCINDIDO);

        if (!valida) {
            throw new IllegalStateException(
                    "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
        }
    }

    private void activar(Contrato contrato) {
        Propiedad propiedad = contrato.getPropiedad();

        if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalStateException("La propiedad no está disponible para ser alquilada.");
        }

        if (contratoRepository.existsByPropiedad_IdAndEstado(propiedad.getId(), EstadoContrato.ACTIVO)) {
            throw new IllegalStateException("La propiedad ya tiene un contrato activo.");
        }

        propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.ALQUILADA);
        propiedadRepository.save(propiedad);
        historialPropiedadRepository.save(
                new HistorialEstadoPropiedad(EstadoDisponibilidad.ALQUILADA, LocalDateTime.now(), propiedad));
    }

    private void liberarPropiedad(Contrato contrato) {
        Propiedad propiedad = contrato.getPropiedad();
        propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        propiedadRepository.save(propiedad);
        historialPropiedadRepository.save(
                new HistorialEstadoPropiedad(EstadoDisponibilidad.DISPONIBLE, LocalDateTime.now(), propiedad));
    }

    private void registrarHistorial(Contrato contrato) {
        historialContratoRepository.save(
                new HistorialEstadoContrato(contrato.getEstado(), LocalDateTime.now(), contrato));
    }
}
