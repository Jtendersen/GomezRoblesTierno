package ar.edu.utn.desi.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Contrato;
import ar.edu.utn.desi.hogarya.model.EstadoContrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse();

    boolean existsByPropiedad_IdAndEstado(Long propiedadId, EstadoContrato estado);
}
