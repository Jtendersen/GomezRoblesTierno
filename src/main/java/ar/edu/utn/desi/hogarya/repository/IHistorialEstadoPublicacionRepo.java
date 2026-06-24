package ar.edu.utn.desi.hogarya.repository;

import ar.edu.utn.desi.hogarya.model.HistorialEstadoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHistorialEstadoPublicacionRepo extends JpaRepository<HistorialEstadoPublicacion, Long> {
}