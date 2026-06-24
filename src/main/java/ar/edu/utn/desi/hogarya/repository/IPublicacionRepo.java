package ar.edu.utn.desi.hogarya.repository;

import ar.edu.utn.desi.hogarya.model.EstadoPublicacion;
import ar.edu.utn.desi.hogarya.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPublicacionRepo extends JpaRepository<Publicacion, Long> {

    // HU 2.1 y 2.3: Verifica si la propiedad ya tiene otra publicación activa (excluyendo una publicación específica en caso de edición)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Publicacion p " +
           "WHERE p.propiedad.id = :idPropiedad AND p.estado = 'ACTIVA' " +
           "AND p.eliminada = false AND (:idPublicacion IS NULL OR p.id <> :idPublicacion)")
    boolean existeActivaParaPropiedad(@Param("idPropiedad") Long idPropiedad, @Param("idPublicacion") Long idPublicacion);

    // HU 2.4: Filtros dinámicos para el listado
    @Query("SELECT p FROM Publicacion p WHERE p.eliminada = false " +
           "AND (:idPropiedad IS NULL OR p.propiedad.id = :idPropiedad) " +
           "AND (:idCiudad IS NULL OR p.propiedad.ciudad.id = :idCiudad) " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> buscarConFiltros(@Param("idPropiedad") Long idPropiedad,
                                       @Param("idCiudad") Long idCiudad,
                                       @Param("estado") EstadoPublicacion estado,
                                       @Param("precioMin") Double precioMin,
                                       @Param("precioMax") Double precioMax);
}