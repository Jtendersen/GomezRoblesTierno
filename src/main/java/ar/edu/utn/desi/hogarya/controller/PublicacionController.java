package ar.edu.utn.desi.hogarya.controller;

import ar.edu.utn.desi.hogarya.exception.PublicacionException;
import ar.edu.utn.desi.hogarya.model.PublicacionForm;
import ar.edu.utn.desi.hogarya.model.EstadoPublicacion;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;
import ar.edu.utn.desi.hogarya.service.IPublicacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {

    @Autowired
    private IPublicacionService publicacionService;

    @Autowired
    private PropiedadRepository propiedadRepo;

    @GetMapping("/alta")
    public String inicializarAlta(ModelMap modelo) {
        modelo.addAttribute("formBean", new PublicacionForm());
        cargarDatosComplementarios(modelo);
        return "publicacionAlta";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("formBean") PublicacionForm formBean, 
                          BindingResult result, 
                          ModelMap modelo) {
        // Validación de sintaxis (Bean Validation)
        if (result.hasErrors()) {
            cargarDatosComplementarios(modelo);
            return "publicacionAlta";
        }

        try {
            // Lógica de negocio
            publicacionService.guardarPublicacion(formBean);
            return "redirect:/publicaciones/listado"; // Redirigir al listado tras éxito
        } catch (PublicacionException e) {
            // Captura de la excepción de reglas de negocio
            modelo.addAttribute("errorNegocio", e.getMessage());
            cargarDatosComplementarios(modelo);
            return "publicacionAlta";
        }
    }

    // Método de soporte para rellenar los combos de Thymeleaf
    private void cargarDatosComplementarios(ModelMap modelo) {
        // En tu proyecto real, deberías filtrar por eliminada=false y DISPONIBLE.
        modelo.addAttribute("listaPropiedades", propiedadRepo.findAll()); 
        modelo.addAttribute("listaEstados", EstadoPublicacion.values());
    }
}