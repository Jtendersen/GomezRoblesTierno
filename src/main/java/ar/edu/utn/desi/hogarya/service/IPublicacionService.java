package ar.edu.utn.desi.hogarya.service;

import ar.edu.utn.desi.hogarya.model.PublicacionForm;
import ar.edu.utn.desi.hogarya.model.EstadoPublicacion;
import ar.edu.utn.desi.hogarya.model.Publicacion;

import java.util.List;

public interface IPublicacionService {
    void guardarPublicacion(PublicacionForm form);
    void bajaLogica(Long idPublicacion);
    List<Publicacion> listarConFiltros(Long idPropiedad, Long idCiudad, EstadoPublicacion estado, Double min, Double max);
}