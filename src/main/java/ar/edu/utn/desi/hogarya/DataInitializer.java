package ar.edu.utn.desi.hogarya;

import ar.edu.utn.desi.hogarya.model.*;
import ar.edu.utn.desi.hogarya.repository.CiudadRepository;
import ar.edu.utn.desi.hogarya.repository.ProvinciaRepository;
import ar.edu.utn.desi.hogarya.repository.PersonaRepository;
import ar.edu.utn.desi.hogarya.service.ContratoService;
import ar.edu.utn.desi.hogarya.service.FacturaService;
import ar.edu.utn.desi.hogarya.service.PropiedadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

	private final CiudadRepository ciudadRepository;
	private final ProvinciaRepository provinciaRepository;
	private final PersonaRepository personaRepository;
	private final PropiedadService propiedadService;
	private final ContratoService contratoService;
	private final FacturaService facturaService;

	public DataInitializer(CiudadRepository ciudadRepository,
	                       ProvinciaRepository provinciaRepository,
	                       PersonaRepository personaRepository,
	                       PropiedadService propiedadService,
	                       ContratoService contratoService,
	                       FacturaService facturaService) {
	    this.ciudadRepository = ciudadRepository;
	    this.provinciaRepository = provinciaRepository;
	    this.personaRepository = personaRepository;
	    this.propiedadService = propiedadService;
	    this.contratoService = contratoService;
	    this.facturaService = facturaService;
	}

    @Override
    public void run(String... args) {
        if (ciudadRepository.count() > 0) return;

        // --- Ciudades ---
        Provincia provSantaFe = provinciaRepository.save(new Provincia("Santa Fe"));
        Provincia provCordoba = provinciaRepository.save(new Provincia("Córdoba"));
        Provincia provBsAs = provinciaRepository.save(new Provincia("Buenos Aires"));

        List<Ciudad> ciudades = ciudadRepository.saveAll(List.of(
                new Ciudad("Santa Fe", provSantaFe),
                new Ciudad("Rosario", provSantaFe),
                new Ciudad("Córdoba", provCordoba),
                new Ciudad("Buenos Aires", provBsAs)
        ));
        
        Ciudad santaFe = ciudades.get(0);
        Ciudad rosario = ciudades.get(1);
        Ciudad cordoba = ciudades.get(2);

        // --- Personas ---
        List<Persona> personas = personaRepository.saveAll(List.of(
                new Persona("Carlos", "Pérez",     "20-11111111-1", "342-4001111", "cperez@mail.com",    "Av. San Martín 100, Santa Fe"),
                new Persona("Ana", "García",        "27-22222222-2", "341-4002222", "agarcia@mail.com",   "Bv. Oroño 200, Rosario"),
                new Persona("Luis", "Martínez",     "20-33333333-3", "351-4003333", "lmartinez@mail.com", "Av. Colón 300, Córdoba"),
                new Persona("María", "López",       "27-44444444-4", "011-40044444", "mlopez@mail.com",   "Corrientes 400, Buenos Aires"),
                new Persona("Roberto", "Fernández", "20-55555555-5", "342-4005555", "rfernandez@mail.com","San Jerónimo 500, Santa Fe")
        ));

        Persona carlos  = personas.get(0);
        Persona ana     = personas.get(1);
        Persona luis    = personas.get(2);
        Persona maria   = personas.get(3);
        Persona roberto = personas.get(4);

        // --- Propiedades ---
        Propiedad p1 = new Propiedad();
        p1.setDireccion("Rivadavia 1234");
        p1.setCiudad(santaFe);
        p1.setTipo(TipoPropiedad.DEPARTAMENTO);
        p1.setCantidadAmbientes(2);
        p1.setMetrosCuadrados(55.0);
        p1.setDescripcion("Departamento luminoso, piso 3, con cochera.");
        p1.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        p1.setPropietario(carlos);
        propiedadService.crear(p1);

        Propiedad p2 = new Propiedad();
        p2.setDireccion("Pellegrini 456");
        p2.setCiudad(rosario);
        p2.setTipo(TipoPropiedad.CASA);
        p2.setCantidadAmbientes(4);
        p2.setMetrosCuadrados(120.0);
        p2.setDescripcion("Casa con jardín y garage doble.");
        p2.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        p2.setPropietario(ana);
        propiedadService.crear(p2);

        Propiedad p3 = new Propiedad();
        p3.setDireccion("Av. Vélez Sársfield 789");
        p3.setCiudad(cordoba);
        p3.setTipo(TipoPropiedad.LOCAL);
        p3.setCantidadAmbientes(1);
        p3.setMetrosCuadrados(80.0);
        p3.setDescripcion("Local comercial en zona céntrica, frente al mercado.");
        p3.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        p3.setPropietario(luis);
        propiedadService.crear(p3);

        Propiedad p4 = new Propiedad();
        p4.setDireccion("Urquiza 321");
        p4.setCiudad(santaFe);
        p4.setTipo(TipoPropiedad.DEPARTAMENTO);
        p4.setCantidadAmbientes(3);
        p4.setMetrosCuadrados(70.0);
        p4.setDescripcion("Departamento en planta baja con patio exclusivo.");
        p4.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        p4.setPropietario(roberto);
        propiedadService.crear(p4);

        // --- Contratos ---

        // Contrato 1: BORRADOR — p1 permanece DISPONIBLE
        Contrato c1 = new Contrato();
        c1.setPropiedad(p1);
        c1.setInquilino(maria);
        c1.setFechaInicio(LocalDate.of(2026, 7, 1));
        c1.setDuracionMeses(12);
        c1.setImporteMensual(new BigDecimal("85000.00"));
        c1.setDiaVencimientoMensual(10);
        c1.setDescripcion("Contrato en borrador, pendiente de firma.");
        contratoService.crear(c1);

        // Contrato 2: ACTIVO — p2 pasa a ALQUILADA
        Contrato c2 = new Contrato();
        c2.setPropiedad(p2);
        c2.setInquilino(roberto);
        c2.setFechaInicio(LocalDate.of(2025, 6, 1));
        c2.setDuracionMeses(24);
        c2.setImporteMensual(new BigDecimal("120000.00"));
        c2.setDiaVencimientoMensual(5);
        c2.setDescripcion("Contrato vigente de alquiler de casa.");
        Contrato c2Guardado = contratoService.crear(c2);
        Contrato c2Activo = contratoService.cambiarEstado(c2Guardado.getId(), EstadoContrato.ACTIVO);

        // Contrato 3: FINALIZADO — p3 vuelve a DISPONIBLE
        Contrato c3 = new Contrato();
        c3.setPropiedad(p3);
        c3.setInquilino(maria);
        c3.setFechaInicio(LocalDate.of(2024, 1, 1));
        c3.setDuracionMeses(12);
        c3.setImporteMensual(new BigDecimal("95000.00"));
        c3.setDiaVencimientoMensual(15);
        c3.setDescripcion("Contrato de local comercial, ya finalizado.");
        Contrato c3Guardado = contratoService.crear(c3);
        contratoService.cambiarEstado(c3Guardado.getId(), EstadoContrato.ACTIVO);
        contratoService.cambiarEstado(c3Guardado.getId(), EstadoContrato.FINALIZADO);

        // --- Facturas (sobre el contrato ACTIVO c2) ---

        // Factura 1: PENDIENTE
        Factura f1 = new Factura();
        f1.setContrato(c2Activo);
        f1.setConceptoFacturado("Alquiler Junio 2026");
        f1.setImporte(new BigDecimal("120000.00"));
        f1.setFechaEmision(LocalDate.of(2026, 6, 1));
        f1.setFechaVencimiento(LocalDate.of(2026, 6, 5));
        facturaService.crear(f1);

        // Factura 2: VENCIDA
        Factura f2 = new Factura();
        f2.setContrato(c2Activo);
        f2.setConceptoFacturado("Alquiler Mayo 2026");
        f2.setImporte(new BigDecimal("120000.00"));
        f2.setFechaEmision(LocalDate.of(2026, 5, 1));
        f2.setFechaVencimiento(LocalDate.of(2026, 5, 5));
        Factura f2Guardada = facturaService.crear(f2);
        facturaService.marcarVencida(f2Guardada.getId());

        // Factura 3: PAGADA con datos de pago completos
        Factura f3 = new Factura();
        f3.setContrato(c2Activo);
        f3.setConceptoFacturado("Alquiler Abril 2026");
        f3.setImporte(new BigDecimal("120000.00"));
        f3.setFechaEmision(LocalDate.of(2026, 4, 1));
        f3.setFechaVencimiento(LocalDate.of(2026, 4, 5));
        Factura f3Guardada = facturaService.crear(f3);
        facturaService.registrarPago(
                f3Guardada.getId(),
                LocalDate.of(2026, 4, 3),
                MedioPago.TRANSFERENCIA,
                new BigDecimal("120000.00"),
                BigDecimal.ZERO
        );
    }
}
