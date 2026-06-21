package ar.edu.utn.desi.hogarya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Ciudad;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
}
