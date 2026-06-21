package ar.edu.utn.desi.hogarya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
