package ar.edu.utn.desi.hogarya.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.desi.hogarya.model.EstadoDisponibilidad;
import ar.edu.utn.desi.hogarya.model.HistorialEstadoPropiedad;
import ar.edu.utn.desi.hogarya.model.Propiedad;
import ar.edu.utn.desi.hogarya.repository.HistorialEstadoPropiedadRepository;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private HistorialEstadoPropiedadRepository historialRepository;

    // ---------- LISTADO ----------
    public List<Propiedad> listarActivas() {
        return propiedadRepository.findByEliminadaFalse();
    }

    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
    }

    // ---------- ALTA ----------
    public Propiedad crear(Propiedad propiedad) {
        // Regla: no puede haber otra propiedad activa con la misma direccion y ciudad
        boolean existe = propiedadRepository.existsByDireccionAndCiudad_IdAndEliminadaFalse(
                propiedad.getDireccion(), propiedad.getCiudad().getId());

        if (existe) {
            throw new IllegalArgumentException(
                    "Ya existe una propiedad activa con esa direccion y ciudad.");
        }

        // Regla: estado por defecto = DISPONIBLE (ya viene asi desde la entidad, pero lo forzamos si vino nulo)
        if (propiedad.getEstadoDisponibilidad() == null) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        propiedad.setEliminada(false);

        Propiedad guardada = propiedadRepository.save(propiedad);

        // Registrar historial del estado inicial
        registrarHistorial(guardada);

        return guardada;
    }

    // ---------- MODIFICACION ----------
    public Propiedad modificar(Long id, Propiedad datosNuevos) {
        Propiedad existente = buscarPorId(id);

        // Si cambia la direccion o ciudad, validar que no quede duplicada
        boolean cambioUbicacion = !existente.getDireccion().equals(datosNuevos.getDireccion())
                || !existente.getCiudad().getId().equals(datosNuevos.getCiudad().getId());

        if (cambioUbicacion) {
            boolean existe = propiedadRepository.existsByDireccionAndCiudad_IdAndEliminadaFalse(
                    datosNuevos.getDireccion(), datosNuevos.getCiudad().getId());
            if (existe) {
                throw new IllegalArgumentException(
                        "Ya existe otra propiedad activa con esa direccion y ciudad.");
            }
        }

        boolean cambioEstado = existente.getEstadoDisponibilidad() != datosNuevos.getEstadoDisponibilidad();

        existente.setDireccion(datosNuevos.getDireccion());
        existente.setCiudad(datosNuevos.getCiudad());
        existente.setTipo(datosNuevos.getTipo());
        existente.setCantidadAmbientes(datosNuevos.getCantidadAmbientes());
        existente.setMetrosCuadrados(datosNuevos.getMetrosCuadrados());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setEstadoDisponibilidad(datosNuevos.getEstadoDisponibilidad());
        existente.setPropietario(datosNuevos.getPropietario());

        Propiedad actualizada = propiedadRepository.save(existente);

        if (cambioEstado) {
            registrarHistorial(actualizada);
        }

        return actualizada;
    }

    // ---------- BAJA (logica) ----------
    public void eliminar(Long id) {
        Propiedad propiedad = buscarPorId(id);

        if (propiedad.getEstadoDisponibilidad() == EstadoDisponibilidad.ALQUILADA) {
            throw new IllegalStateException(
                    "No se puede eliminar una propiedad con un contrato activo vigente.");
        }

        propiedad.setEliminada(true);
        propiedadRepository.save(propiedad);
    }

    // ---------- AUXILIAR: registrar historial ----------
    private void registrarHistorial(Propiedad propiedad) {
        HistorialEstadoPropiedad historial = new HistorialEstadoPropiedad(
                propiedad.getEstadoDisponibilidad(),
                LocalDateTime.now(),
                propiedad
        );
        historialRepository.save(historial);
    }
}