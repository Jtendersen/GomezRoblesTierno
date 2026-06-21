package ar.edu.utn.desi.hogarya;

import ar.edu.utn.desi.hogarya.model.*;
import ar.edu.utn.desi.hogarya.repository.CiudadRepository;
import ar.edu.utn.desi.hogarya.repository.PersonaRepository;
import ar.edu.utn.desi.hogarya.service.PropiedadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CiudadRepository ciudadRepository;
    private final PersonaRepository personaRepository;
    private final PropiedadService propiedadService;

    public DataInitializer(CiudadRepository ciudadRepository,
                           PersonaRepository personaRepository,
                           PropiedadService propiedadService) {
        this.ciudadRepository = ciudadRepository;
        this.personaRepository = personaRepository;
        this.propiedadService = propiedadService;
    }

    @Override
    public void run(String... args) {
        if (ciudadRepository.count() > 0) return;

        List<Ciudad> ciudades = ciudadRepository.saveAll(List.of(
                new Ciudad("Santa Fe"),
                new Ciudad("Rosario"),
                new Ciudad("Córdoba"),
                new Ciudad("Buenos Aires")
        ));

        Ciudad santaFe  = ciudades.get(0);
        Ciudad rosario  = ciudades.get(1);
        Ciudad cordoba  = ciudades.get(2);
        Ciudad bsAs     = ciudades.get(3);

        List<Persona> personas = personaRepository.saveAll(List.of(
                new Persona("Carlos Pérez",     "20-11111111-1", "342-4001111", "cperez@mail.com",    "Av. San Martín 100, Santa Fe"),
                new Persona("Ana García",        "27-22222222-2", "341-4002222", "agarcia@mail.com",   "Bv. Oroño 200, Rosario"),
                new Persona("Luis Martínez",     "20-33333333-3", "351-4003333", "lmartinez@mail.com", "Av. Colón 300, Córdoba"),
                new Persona("María López",       "27-44444444-4", "011-40044444", "mlopez@mail.com",   "Corrientes 400, Buenos Aires"),
                new Persona("Roberto Fernández", "20-55555555-5", "342-4005555", "rfernandez@mail.com","San Jerónimo 500, Santa Fe")
        ));

        Persona carlos   = personas.get(0);
        Persona ana      = personas.get(1);
        Persona luis     = personas.get(2);
        Persona roberto  = personas.get(4);

        // Propiedades DISPONIBLES — necesarias para crear Publicaciones y Contratos
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
    }
}
