package ar.edu.utn.desi.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Propiedad;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    // Trae todas las propiedades NO eliminadas (para el listado)
    List<Propiedad> findByEliminadaFalse();

    // Busca si ya existe una propiedad activa con esa direccion y ciudad (para validar duplicados)
    boolean existsByDireccionAndCiudad_IdAndEliminadaFalse(String direccion, Long ciudadId);

}