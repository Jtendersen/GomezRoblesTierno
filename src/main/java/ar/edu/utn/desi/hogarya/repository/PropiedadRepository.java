package ar.edu.utn.desi.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.edu.utn.desi.hogarya.model.EstadoDisponibilidad;
import ar.edu.utn.desi.hogarya.model.Propiedad;
import ar.edu.utn.desi.hogarya.model.TipoPropiedad;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEliminadaFalse();

    boolean existsByDireccionAndCiudad_IdAndEliminadaFalse(String direccion, Long ciudadId);

    @Query("SELECT p FROM Propiedad p WHERE p.eliminada = false " +
           "AND (:direccion IS NULL OR LOWER(p.direccion) LIKE LOWER(CONCAT('%', :direccion, '%'))) " +
           "AND (:ciudadId IS NULL OR p.ciudad.id = :ciudadId) " +
           "AND (:tipo IS NULL OR p.tipo = :tipo) " +
           "AND (:estado IS NULL OR p.estadoDisponibilidad = :estado)")
    List<Propiedad> buscarConFiltros(
            @Param("direccion") String direccion,
            @Param("ciudadId") Long ciudadId,
            @Param("tipo") TipoPropiedad tipo,
            @Param("estado") EstadoDisponibilidad estado);

}