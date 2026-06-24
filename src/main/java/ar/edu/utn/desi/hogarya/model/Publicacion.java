package ar.edu.utn.desi.hogarya.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "propiedad_id", nullable = false)
    private Propiedad propiedad;

    @Column(nullable = false)
    private Double precioMensual;

    private String condiciones;
    private String descripcion;
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPublicacion estado;

    private boolean eliminada = false;

    // Constructores
    public Publicacion() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Propiedad getPropiedad() { return propiedad; }
    public void setPropiedad(Propiedad propiedad) { this.propiedad = propiedad; }
    public Double getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(Double precioMensual) { this.precioMensual = precioMensual; }
    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }
    public boolean isEliminada() { return eliminada; }
    public void setEliminada(boolean eliminada) { this.eliminada = eliminada; }
}