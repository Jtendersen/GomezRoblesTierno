package ar.edu.utn.desi.hogarya.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

import ar.edu.utn.desi.hogarya.model.Contrato;
import ar.edu.utn.desi.hogarya.model.EstadoContrato;
import ar.edu.utn.desi.hogarya.model.Factura;
import ar.edu.utn.desi.hogarya.model.MedioPago;
import ar.edu.utn.desi.hogarya.repository.ContratoRepository;
import ar.edu.utn.desi.hogarya.service.FacturaService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ContratoRepository contratoRepository;

    // ---------- LISTADO ----------
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("facturas", facturaService.listarActivas());
        return "facturas/listado";
    }

    // ---------- FORMULARIO DE ALTA ----------
    @GetMapping("/nueva")
    public String formularioAlta(Model model) {
        model.addAttribute("factura", new Factura());
        model.addAttribute("contratos", contratosActivos());
        return "facturas/formulario";
    }

    // ---------- GUARDAR ALTA ----------
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("factura") Factura factura, Model model) {
        try {
            // Spring solo enlaza el id del contrato; hay que cargar el objeto completo
            if (factura.getContrato() != null && factura.getContrato().getId() != null) {
                Contrato contrato = contratoRepository.findById(factura.getContrato().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado"));
                factura.setContrato(contrato);
            }
            facturaService.crear(factura);
            return "redirect:/facturas";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contratos", contratosActivos());
            return "facturas/formulario";
        }
    }

    // ---------- FORMULARIO DE EDICION ----------
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        try {
            Factura factura = facturaService.buscarPorId(id);
            model.addAttribute("factura", factura);
            model.addAttribute("contratos", contratosActivos());
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
            Factura factura = facturaService.buscarPorId(id);
            model.addAttribute("factura", factura);
            model.addAttribute("contratos", contratosActivos());
            return "facturas/formulario";
        }
    }

    // ---------- ELIMINAR (baja logica) ----------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            facturaService.eliminar(id);
        } catch (Exception e) {
            // Si no se puede eliminar, vuelve al listado sin romper
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
    public String vencer(@PathVariable Long id) {
        try {
            facturaService.marcarVencida(id);
        } catch (Exception e) {
            // vuelve al listado
        }
        return "redirect:/facturas";
    }

    // ---------- ANULAR ----------
    @GetMapping("/anular/{id}")
    public String anular(@PathVariable Long id) {
        try {
            facturaService.anular(id);
        } catch (Exception e) {
            // vuelve al listado
        }
        return "redirect:/facturas";
    }

    // Helper: solo contratos ACTIVOS y no eliminados para el dropdown
    private List<Contrato> contratosActivos() {
        return contratoRepository.findAll().stream()
                .filter(c -> c.getEstado() == EstadoContrato.ACTIVO && !c.isEliminado())
                .collect(Collectors.toList());
    }
}
