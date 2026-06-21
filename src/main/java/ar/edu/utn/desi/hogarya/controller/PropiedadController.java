package ar.edu.utn.desi.hogarya.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ar.edu.utn.desi.hogarya.model.Propiedad;
import ar.edu.utn.desi.hogarya.repository.CiudadRepository;
import ar.edu.utn.desi.hogarya.repository.PersonaRepository;
import ar.edu.utn.desi.hogarya.service.PropiedadService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping
    public String listar(Model model) {
        List<Propiedad> propiedades = propiedadService.listarActivas();
        model.addAttribute("propiedades", propiedades);
        return "propiedades/listado";
    }

    @GetMapping("/nueva")
    public String formularioAlta(Model model) {
        model.addAttribute("propiedad", new Propiedad());
        model.addAttribute("ciudades", ciudadRepository.findAll());
        model.addAttribute("propietarios", personaRepository.findAll());
        return "propiedades/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("propiedad") Propiedad propiedad, Model model) {
        try {
            if (propiedad.getId() == null) {
                propiedadService.crear(propiedad);
            } else {
                propiedadService.modificar(propiedad.getId(), propiedad);
            }
            return "redirect:/propiedades";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ciudades", ciudadRepository.findAll());
            model.addAttribute("propietarios", personaRepository.findAll());
            return "propiedades/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("propiedad", propiedadService.buscarPorId(id));
        model.addAttribute("ciudades", ciudadRepository.findAll());
        model.addAttribute("propietarios", personaRepository.findAll());
        return "propiedades/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        propiedadService.eliminar(id);
        return "redirect:/propiedades";
    }
}
