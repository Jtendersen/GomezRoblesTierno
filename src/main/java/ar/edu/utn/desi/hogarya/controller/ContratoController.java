package ar.edu.utn.desi.hogarya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.utn.desi.hogarya.model.Contrato;
import ar.edu.utn.desi.hogarya.model.EstadoContrato;
import ar.edu.utn.desi.hogarya.repository.PersonaRepository;
import ar.edu.utn.desi.hogarya.repository.PropiedadRepository;
import ar.edu.utn.desi.hogarya.service.ContratoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contratos", contratoService.listarActivos());
        return "contratos/listado";
    }

    @GetMapping("/nuevo")
    public String formularioAlta(Model model) {
        model.addAttribute("contrato", new Contrato());
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("contrato", contratoService.buscarPorId(id));
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("contrato") Contrato contrato, Model model) {
        try {
            if (contrato.getId() == null) {
                contratoService.crear(contrato);
            } else {
                contratoService.modificar(contrato.getId(), contrato);
            }
            return "redirect:/contratos";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatosFormulario(model);
            return "contratos/formulario";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contratoService.eliminar(id);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratos";
    }

    @GetMapping("/cambiarEstado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoContrato nuevoEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            contratoService.cambiarEstado(id, nuevoEstado);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contratos";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("inquilinos", personaRepository.findAll());
    }
}
