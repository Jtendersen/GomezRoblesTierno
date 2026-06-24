package ar.edu.utn.desi.hogarya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.utn.desi.hogarya.model.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findByEliminadaFalse();

    List<Factura> findByContrato_IdAndEliminadaFalse(Long contratoId);
}
