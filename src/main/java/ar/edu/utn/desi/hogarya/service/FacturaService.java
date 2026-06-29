package ar.edu.utn.desi.hogarya.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.desi.hogarya.model.EstadoContrato;
import ar.edu.utn.desi.hogarya.model.EstadoFactura;
import ar.edu.utn.desi.hogarya.model.Factura;
import ar.edu.utn.desi.hogarya.model.HistorialEstadoFactura;
import ar.edu.utn.desi.hogarya.model.MedioPago;
import ar.edu.utn.desi.hogarya.repository.ContratoRepository;
import ar.edu.utn.desi.hogarya.repository.FacturaRepository;
import ar.edu.utn.desi.hogarya.repository.HistorialEstadoFacturaRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private HistorialEstadoFacturaRepository historialRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    public List<Factura> listarActivas() {
        return facturaRepository.findByEliminadaFalse();
    }

    public List<Factura> buscarConFiltros(Long contratoId, Long propiedadId, Long inquilinoId,
                                          EstadoFactura estado, LocalDate fechaDesde, LocalDate fechaHasta) {
        return facturaRepository.findByEliminadaFalse().stream()
                .filter(f -> contratoId  == null || f.getContrato().getId().equals(contratoId))
                .filter(f -> propiedadId == null || f.getContrato().getPropiedad().getId().equals(propiedadId))
                .filter(f -> inquilinoId == null || f.getContrato().getInquilino().getId().equals(inquilinoId))
                .filter(f -> estado      == null || f.getEstado() == estado)
                .filter(f -> fechaDesde  == null || !f.getFechaVencimiento().isBefore(fechaDesde))
                .filter(f -> fechaHasta  == null || !f.getFechaVencimiento().isAfter(fechaHasta))
                .collect(Collectors.toList());
    }

    public Factura buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    public Factura crear(Factura factura) {
        // Regla: solo contratos ACTIVOS y no eliminados
        if (factura.getContrato() == null ||
                factura.getContrato().getEstado() != EstadoContrato.ACTIVO ||
                factura.getContrato().isEliminado()) {
            throw new IllegalArgumentException(
                    "Solo se puede facturar sobre contratos activos y vigentes.");
        }

        // Regla: fecha vencimiento >= fecha emision
        if (factura.getFechaVencimiento().isBefore(factura.getFechaEmision())) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser anterior a la fecha de emision.");
        }

        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setEliminada(false);
        // Los datos de pago siempre null al crear
        factura.setFechaPago(null);
        factura.setMedio(null);
        factura.setImportePagado(null);
        factura.setInteres(null);

        Factura guardada = facturaRepository.save(factura);
        registrarHistorial(guardada);
        return guardada;
    }

    public Factura modificar(Long id, Factura datos) {
        Factura existente = buscarPorId(id);

        // Regla: no se puede modificar si PAGADA o ANULADA
        if (existente.getEstado() == EstadoFactura.PAGADA ||
                existente.getEstado() == EstadoFactura.ANULADA) {
            throw new IllegalStateException(
                    "No se puede modificar una factura en estado " + existente.getEstado() + ".");
        }

        // Regla: fecha vencimiento >= fecha emision
        if (datos.getFechaVencimiento().isBefore(datos.getFechaEmision())) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento no puede ser anterior a la fecha de emision.");
        }

        existente.setConceptoFacturado(datos.getConceptoFacturado());
        existente.setImporte(datos.getImporte());
        existente.setFechaEmision(datos.getFechaEmision());
        existente.setFechaVencimiento(datos.getFechaVencimiento());

        return facturaRepository.save(existente);
    }

    public void eliminar(Long id) {
        Factura factura = buscarPorId(id);

        // Regla: no se puede eliminar si PAGADA o ANULADA
        if (factura.getEstado() == EstadoFactura.PAGADA ||
                factura.getEstado() == EstadoFactura.ANULADA) {
            throw new IllegalStateException(
                    "No se puede eliminar una factura en estado " + factura.getEstado() + ".");
        }

        factura.setEliminada(true);
        facturaRepository.save(factura);
    }

    public Factura marcarVencida(Long id) {
        Factura factura = buscarPorId(id);

        if (factura.getEstado() != EstadoFactura.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede marcar como vencida una factura PENDIENTE.");
        }

        factura.setEstado(EstadoFactura.VENCIDA);
        Factura actualizada = facturaRepository.save(factura);
        registrarHistorial(actualizada);
        return actualizada;
    }

    public Factura anular(Long id) {
        Factura factura = buscarPorId(id);

        if (factura.getEstado() != EstadoFactura.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede anular una factura PENDIENTE.");
        }

        factura.setEstado(EstadoFactura.ANULADA);
        Factura actualizada = facturaRepository.save(factura);
        registrarHistorial(actualizada);
        return actualizada;
    }

    public Factura registrarPago(Long id, LocalDate fechaPago, MedioPago medio,
                                 BigDecimal importePagado, BigDecimal interes) {
        Factura factura = buscarPorId(id);

        // Regla: solo PENDIENTE o VENCIDA pueden pasar a PAGADA
        if (factura.getEstado() != EstadoFactura.PENDIENTE &&
                factura.getEstado() != EstadoFactura.VENCIDA) {
            throw new IllegalStateException(
                    "Solo se puede registrar pago de facturas PENDIENTES o VENCIDAS.");
        }

        // Regla: datos de pago obligatorios
        if (fechaPago == null || medio == null || importePagado == null) {
            throw new IllegalArgumentException(
                    "Fecha de pago, medio y monto pagado son obligatorios.");
        }

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(fechaPago);
        factura.setMedio(medio);
        factura.setImportePagado(importePagado);
        factura.setInteres(interes != null ? interes : BigDecimal.ZERO);

        Factura actualizada = facturaRepository.save(factura);
        registrarHistorial(actualizada);
        return actualizada;
    }

    private void registrarHistorial(Factura factura) {
        HistorialEstadoFactura historial = new HistorialEstadoFactura(
                factura.getEstado(),
                LocalDateTime.now(),
                factura
        );
        historialRepository.save(historial);
    }
}
