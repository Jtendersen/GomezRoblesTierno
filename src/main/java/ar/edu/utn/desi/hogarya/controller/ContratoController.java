package ar.edu.utn.desi.hogarya.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import ar.edu.utn.desi.hogarya.service.ContratoService;
import ar.edu.utn.desi.hogarya.service.PersonaService;
import ar.edu.utn.desi.hogarya.service.PropiedadService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long inquilinoId,
            @RequestParam(required = false) EstadoContrato estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            Model model) {

        model.addAttribute("contratos",
                contratoService.buscarConFiltros(propiedadId, inquilinoId, estado, fechaDesde));

        model.addAttribute("propiedades", propiedadService.listarActivas());
        model.addAttribute("inquilinos",  personaService.listarTodas());
        model.addAttribute("estados",     EstadoContrato.values());

        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroInquilinoId", inquilinoId);
        model.addAttribute("filtroEstado",      estado);
        model.addAttribute("filtroFechaDesde",  fechaDesde);

        return "contratos/listado";
    }

    @GetMapping("/nuevo")
    public String formularioAlta(Model model) {
        model.addAttribute("contrato", new Contrato());
        cargarDatosFormulario(model);
        return "contratos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        Contrato contrato = contratoService.buscarPorId(id);
        if (contrato.getEstado() != EstadoContrato.BORRADOR) {
            redirectAttributes.addFlashAttribute("error",
                    "Solo se pueden editar contratos en estado BORRADOR.");
            return "redirect:/contratos";
        }
        model.addAttribute("contrato", contrato);
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
        model.addAttribute("propiedades", propiedadService.listarActivas());
        model.addAttribute("inquilinos", personaService.listarTodas());
    }
}
