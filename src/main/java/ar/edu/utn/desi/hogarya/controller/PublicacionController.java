package ar.edu.utn.desi.hogarya.controller;

import ar.edu.utn.desi.hogarya.exception.PublicacionException;
import ar.edu.utn.desi.hogarya.model.EstadoPublicacion;
import ar.edu.utn.desi.hogarya.model.Publicacion;
import ar.edu.utn.desi.hogarya.model.PublicacionForm;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;
import ar.edu.utn.desi.hogarya.service.IPublicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {

    @Autowired
    private IPublicacionService publicacionService;

    @Autowired
    private PropiedadRepository propiedadRepo;

    // ---------- LISTADO CON FILTROS (HU 2.4) ----------
    @GetMapping
    public String listar(
            @RequestParam(required = false) Long idPropiedad,
            @RequestParam(required = false) Long idCiudad,
            @RequestParam(required = false) EstadoPublicacion estado,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            ModelMap modelo) {

        List<Publicacion> lista = publicacionService.listarConFiltros(idPropiedad, idCiudad, estado, precioMin, precioMax);
        modelo.addAttribute("publicaciones", lista);
        modelo.addAttribute("listaEstados", EstadoPublicacion.values());
        modelo.addAttribute("listaPropiedades", propiedadRepo.findByEliminadaFalse());
        modelo.addAttribute("filtroIdPropiedad", idPropiedad);
        modelo.addAttribute("filtroIdCiudad", idCiudad);
        modelo.addAttribute("filtroEstado", estado);
        modelo.addAttribute("filtroPrecioMin", precioMin);
        modelo.addAttribute("filtroPrecioMax", precioMax);
        return "publicaciones/listado";
    }

    // ---------- FORMULARIO ALTA (HU 2.1) ----------
    @GetMapping("/alta")
    public String inicializarAlta(ModelMap modelo) {
        modelo.addAttribute("formBean", new PublicacionForm());
        cargarDatosComplementarios(modelo);
        return "publicaciones/formulario";
    }

    // ---------- GUARDAR ALTA/EDICIÓN (HU 2.1 / HU 2.3) ----------
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("formBean") PublicacionForm formBean,
                          BindingResult result,
                          ModelMap modelo,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            cargarDatosComplementarios(modelo);
            return "publicaciones/formulario";
        }
        try {
            publicacionService.guardarPublicacion(formBean);
            redirectAttributes.addFlashAttribute("exito", "Publicación guardada correctamente.");
            return "redirect:/publicaciones";
        } catch (PublicacionException e) {
            modelo.addAttribute("errorNegocio", e.getMessage());
            cargarDatosComplementarios(modelo);
            return "publicaciones/formulario";
        }
    }

    // ---------- FORMULARIO EDICIÓN (HU 2.3) ----------
    @GetMapping("/editar/{id}")
    public String inicializarEdicion(@PathVariable Long id, ModelMap modelo, RedirectAttributes redirectAttributes) {
        try {
            Publicacion pub = publicacionService.buscarPorId(id);

            PublicacionForm formBean = new PublicacionForm();
            formBean.setId(pub.getId());
            formBean.setIdPropiedad(pub.getPropiedad().getId());
            formBean.setPrecioMensual(pub.getPrecioMensual());
            formBean.setCondiciones(pub.getCondiciones());
            formBean.setDescripcion(pub.getDescripcion());
            formBean.setFechaPublicacion(pub.getFechaPublicacion());
            formBean.setEstado(pub.getEstado());

            modelo.addAttribute("formBean", formBean);
            cargarDatosComplementarios(modelo);
            return "publicaciones/formulario";
        } catch (PublicacionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/publicaciones";
        }
    }

    // ---------- ELIMINAR / BAJA LÓGICA (HU 2.2) ----------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            publicacionService.bajaLogica(id);
            redirectAttributes.addFlashAttribute("exito", "Publicación eliminada correctamente.");
        } catch (PublicacionException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/publicaciones";
    }

    // ---------- SOPORTE ----------
    private void cargarDatosComplementarios(ModelMap modelo) {
        modelo.addAttribute("listaPropiedades", propiedadRepo.findByEliminadaFalse());
        modelo.addAttribute("listaEstados", EstadoPublicacion.values());
    }
}
