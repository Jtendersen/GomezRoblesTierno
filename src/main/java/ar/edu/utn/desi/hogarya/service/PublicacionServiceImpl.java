package ar.edu.utn.desi.hogarya.service;

import ar.edu.utn.desi.hogarya.exception.PublicacionException;
import ar.edu.utn.desi.hogarya.model.*;
import ar.edu.utn.desi.hogarya.repository.IHistorialEstadoPublicacionRepo;
import ar.edu.utn.desi.hogarya.repository.IPublicacionRepo;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PublicacionServiceImpl implements IPublicacionService {

    @Autowired
    private IPublicacionRepo publicacionRepo;

    @Autowired
    private PropiedadRepository propiedadRepo;

    @Autowired
    private IHistorialEstadoPublicacionRepo historialRepo;

    @Override
    @Transactional
    public void guardarPublicacion(PublicacionForm form) {
        Propiedad propiedad = propiedadRepo.findById(form.getIdPropiedad())
                .orElseThrow(() -> new PublicacionException("La propiedad seleccionada no existe."));

        // Regla: la propiedad debe estar disponible y no eliminada
        if (propiedad.isEliminada() || propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new PublicacionException("La propiedad debe estar DISPONIBLE para poder ser publicada.");
        }

        // Regla: no puede haber otra publicación activa para la misma propiedad
        if (form.getEstado() == EstadoPublicacion.ACTIVA) {
            boolean existeOtraActiva = publicacionRepo.existeActivaParaPropiedad(propiedad.getId(), form.getId());
            if (existeOtraActiva) {
                throw new PublicacionException("Ya existe una publicación ACTIVA para esta propiedad. Debe pausarla o finalizarla primero.");
            }
        }

        Publicacion pub;
        EstadoPublicacion estadoAnterior = null;

        if (form.getId() != null) {
            // Edición
            pub = publicacionRepo.findById(form.getId())
                    .orElseThrow(() -> new PublicacionException("Publicación no encontrada."));

            // Regla: no se puede modificar una publicación finalizada (condiciones)
            if (pub.getEstado() == EstadoPublicacion.FINALIZADA) {
                throw new PublicacionException("No se pueden modificar las condiciones de una publicación FINALIZADA.");
            }

            estadoAnterior = pub.getEstado();
            // La propiedad no se puede cambiar en edición (HU 2.3)
            // La fecha de publicación se mantiene la original si no se envía nueva
            if (form.getFechaPublicacion() != null) {
                pub.setFechaPublicacion(form.getFechaPublicacion());
            }
        } else {
            // Alta
            pub = new Publicacion();
            pub.setFechaPublicacion(form.getFechaPublicacion());
        }

        pub.setPropiedad(propiedad);
        pub.setPrecioMensual(form.getPrecioMensual());
        pub.setCondiciones(form.getCondiciones());
        pub.setDescripcion(form.getDescripcion());
        pub.setEstado(form.getEstado());

        publicacionRepo.save(pub);

        // Registrar historial solo si es nueva o si el estado cambió
        if (form.getId() == null || estadoAnterior != form.getEstado()) {
            HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion(
                    pub.getEstado(), LocalDateTime.now(), pub);
            historialRepo.save(historial);
        }
    }

    @Override
    @Transactional
    public void bajaLogica(Long idPublicacion) {
        Publicacion pub = publicacionRepo.findById(idPublicacion)
                .orElseThrow(() -> new PublicacionException("Publicación no encontrada."));

        // Regla HU 2.2: solo se pueden eliminar publicaciones ACTIVAS
        if (pub.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new PublicacionException("Solo se pueden eliminar publicaciones en estado ACTIVA.");
        }

        pub.setEliminada(true);
        publicacionRepo.save(pub);
    }

    @Override
    public Publicacion buscarPorId(Long id) {
        return publicacionRepo.findById(id)
                .orElseThrow(() -> new PublicacionException("Publicación no encontrada."));
    }

    @Override
    public List<Publicacion> listarConFiltros(Long idPropiedad, Long idCiudad, EstadoPublicacion estado, BigDecimal min, BigDecimal max) {
        return publicacionRepo.buscarConFiltros(idPropiedad, idCiudad, estado, min, max);
    }
}