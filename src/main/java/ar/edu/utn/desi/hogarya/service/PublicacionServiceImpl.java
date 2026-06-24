package ar.edu.utn.desi.hogarya.service;

import ar.edu.utn.desi.hogarya.exception.PublicacionException;
import ar.edu.utn.desi.hogarya.model.PublicacionForm;
import ar.edu.utn.desi.hogarya.model.*;
import ar.edu.utn.desi.hogarya.repository.IHistorialEstadoPublicacionRepo;
import ar.edu.utn.desi.hogarya.repository.IPublicacionRepo;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        // Regla: La propiedad debe estar disponible para publicarse
        if (propiedad.isEliminada() || propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
            throw new PublicacionException("La propiedad debe estar DISPONIBLE para poder ser publicada.");
        }

        // Regla: No duplicar publicaciones activas simultáneamente
        if (form.getEstado() == EstadoPublicacion.ACTIVA) {
            boolean existeOtraActiva = publicacionRepo.existeActivaParaPropiedad(propiedad.getId(), form.getId());
            if (existeOtraActiva) {
                throw new PublicacionException("Ya existe una publicación ACTIVA para esta propiedad. Debe pausarla o finalizarla primero.");
            }
        }

        Publicacion pub;
        EstadoPublicacion estadoAnterior = null;

        if (form.getId() != null) {
            pub = publicacionRepo.findById(form.getId())
                    .orElseThrow(() -> new PublicacionException("Publicación no encontrada."));
            estadoAnterior = pub.getEstado();
        } else {
            pub = new Publicacion();
            pub.setFecha(LocalDate.now());
        }

        pub.setPropiedad(propiedad);
        pub.setPrecioMensual(form.getPrecioMensual());
        pub.setCondiciones(form.getCondiciones());
        pub.setDescripcion(form.getDescripcion());
        pub.setEstado(form.getEstado());
        
        publicacionRepo.save(pub);

        // Generar historial solo si es nueva o si el estado cambió
        if (form.getId() == null || estadoAnterior != form.getEstado()) {
            HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion(pub.getEstado(), LocalDateTime.now(), pub);
            historialRepo.save(historial);
        }
    }

    @Override
    @Transactional
    public void bajaLogica(Long idPublicacion) {
        Publicacion pub = publicacionRepo.findById(idPublicacion)
                .orElseThrow(() -> new PublicacionException("Publicación no encontrada."));

        // Regla de HU 2.2: Solo se pueden borrar en estado "activa"
        if (pub.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new PublicacionException("Solo se pueden eliminar publicaciones que se encuentren en estado ACTIVA.");
        }

        pub.setEliminada(true);
        publicacionRepo.save(pub);
    }

    @Override
    public List<Publicacion> listarConFiltros(Long idPropiedad, Long idCiudad, EstadoPublicacion estado, Double min, Double max) {
        return publicacionRepo.buscarConFiltros(idPropiedad, idCiudad, estado, min, max);
    }
}