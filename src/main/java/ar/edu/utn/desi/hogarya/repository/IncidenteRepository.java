package ar.edu.utn.desi.hogarya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Incidente;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
}
