package ar.edu.utn.desi.hogarya.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class HistorialEstadoIncidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;

    private LocalDateTime fechaHora;

    @ManyToOne
    private Incidente incidente;

    public HistorialEstadoIncidente() {
    }

    public HistorialEstadoIncidente(EstadoIncidente estado, LocalDateTime fechaHora, Incidente incidente) {
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.incidente = incidente;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoIncidente getEstado() { return estado; }
    public void setEstado(EstadoIncidente estado) { this.estado = estado; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public Incidente getIncidente() { return incidente; }
    public void setIncidente(Incidente incidente) { this.incidente = incidente; }
}
