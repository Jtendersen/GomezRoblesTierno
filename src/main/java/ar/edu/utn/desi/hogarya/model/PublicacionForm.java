package ar.edu.utn.desi.hogarya.model;

import ar.edu.utn.desi.hogarya.model.EstadoPublicacion;
import jakarta.validation.constraints.*;

public class PublicacionForm {

    private Long id; // Para la modificación

    @NotNull(message = "Debe seleccionar una propiedad")
    private Long idPropiedad;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio mensual debe ser mayor a 0")
    private Double precioMensual;

    @NotBlank(message = "Las condiciones no pueden estar vacías")
    private String condiciones;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "Debe definir un estado")
    private EstadoPublicacion estado;

    // Getters y Setters (Generarlos en el IDE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdPropiedad() { return idPropiedad; }
    public void setIdPropiedad(Long idPropiedad) { this.idPropiedad = idPropiedad; }
    public Double getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(Double precioMensual) { this.precioMensual = precioMensual; }
    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }
}