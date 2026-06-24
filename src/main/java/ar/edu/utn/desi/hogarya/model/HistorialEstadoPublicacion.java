package ar.edu.utn.desi.hogarya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistorialEstadoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPublicacion estado;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    public HistorialEstadoPublicacion() {}

    public HistorialEstadoPublicacion(EstadoPublicacion estado, LocalDateTime fechaHora, Publicacion publicacion) {
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.publicacion = publicacion;
    }

    // Getters y Setters (Omitidos por brevedad, generarlos en el IDE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Publicacion getPublicacion() { return publicacion; }
    public void setPublicacion(Publicacion publicacion) { this.publicacion = publicacion; }
}