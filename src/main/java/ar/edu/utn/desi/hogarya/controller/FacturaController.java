package ar.edu.utn.desi.hogarya.controller;

import java.math.BigDecimal;
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
import ar.edu.utn.desi.hogarya.model.EstadoFactura;
import ar.edu.utn.desi.hogarya.model.Factura;
import ar.edu.utn.desi.hogarya.model.MedioPago;
import ar.edu.utn.desi.hogarya.service.ContratoService;
import ar.edu.utn.desi.hogarya.service.FacturaService;
import ar.edu.utn.desi.hogarya.service.PersonaService;
import ar.edu.utn.desi.hogarya.service.PropiedadService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    // ---------- LISTADO ----------
    @GetMapping
    public String listar(
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long inquilinoId,
            @RequestParam(required = false) EstadoFactura estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        model.addAttribute("facturas",
                facturaService.buscarConFiltros(contratoId, propiedadId, inquilinoId, estado, fechaDesde, fechaHasta));

        model.addAttribute("contratos",   contratoService.listarVigentes());
        model.addAttribute("propiedades", propiedadService.listarActivas());
        model.addAttribute("inquilinos",  personaService.listarTodas());
        model.addAttribute("estados",     EstadoFactura.values());

        model.addAttribute("filtroContratoId",  contratoId);
        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroInquilinoId", inquilinoId);
        model.addAttribute("filtroEstado",      estado);
        model.addAttribute("filtroFechaDesde",  fechaDesde);
        model.addAttribute("filtroFechaHasta",  fechaHasta);

        return "facturas/listado";
    }

    // ---------- FORMULARIO DE ALTA ----------
    @GetMapping("/nueva")
    public String formularioAlta(Model model) {
        model.addAttribute("factura", new Factura());
        model.addAttribute("contratos", contratoService.listarVigentes());
        return "facturas/formulario";
    }

    // ---------- GUARDAR ALTA ----------
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("factura") Factura factura, Model model) {
        try {
            if (factura.getContrato() != null && factura.getContrato().getId() != null) {
                Contrato contrato = contratoService.buscarPorId(factura.getContrato().getId());
                factura.setContrato(contrato);
            }
            facturaService.crear(factura);
            return "redirect:/facturas";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contratos", contratoService.listarVigentes());
            return "facturas/formulario";
        }
    }

    // ---------- FORMULARIO DE EDICION ----------
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            Factura factura = facturaService.buscarPorId(id);
            if (factura.getEstado() == EstadoFactura.PAGADA ||
                    factura.getEstado() == EstadoFactura.ANULADA) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede modificar una factura en estado " + factura.getEstado() + ".");
                return "redirect:/facturas";
            }
            model.addAttribute("factura", factura);
            model.addAttribute("contratos", contratoService.listarVigentes());
            return "facturas/formulario";
        } catch (Exception e) {
            return "redirect:/facturas";
        }
    }

    // ---------- GUARDAR EDICION ----------
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute("factura") Factura datos,
                             Model model) {
        try {
            facturaService.modificar(id, datos);
            return "redirect:/facturas";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("factura", facturaService.buscarPorId(id));
            model.addAttribute("contratos", contratoService.listarVigentes());
            return "facturas/formulario";
        }
    }

    // ---------- ELIMINAR (baja logica) ----------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facturaService.eliminar(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/facturas";
    }

    // ---------- FORMULARIO DE PAGO ----------
    @GetMapping("/pagar/{id}")
    public String formularioPago(@PathVariable Long id, Model model) {
        model.addAttribute("factura", facturaService.buscarPorId(id));
        model.addAttribute("medios", MedioPago.values());
        return "facturas/pago";
    }

    // ---------- REGISTRAR PAGO ----------
    @PostMapping("/pagar/{id}")
    public String registrarPago(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPago,
            @RequestParam MedioPago medio,
            @RequestParam BigDecimal importePagado,
            @RequestParam(required = false) BigDecimal interes,
            Model model) {
        try {
            facturaService.registrarPago(id, fechaPago, medio, importePagado, interes);
            return "redirect:/facturas";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("factura", facturaService.buscarPorId(id));
            model.addAttribute("medios", MedioPago.values());
            return "facturas/pago";
        }
    }

    // ---------- MARCAR VENCIDA ----------
    @GetMapping("/vencer/{id}")
    public String vencer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facturaService.marcarVencida(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/facturas";
    }

    // ---------- ANULAR ----------
    @GetMapping("/anular/{id}")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facturaService.anular(id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/facturas";
    }
}
