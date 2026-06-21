package ar.edu.utn.desi.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Propiedad;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEliminadaFalse();

    boolean existsByDireccionAndCiudad_IdAndEliminadaFalse(String direccion, Long ciudadId);
}
