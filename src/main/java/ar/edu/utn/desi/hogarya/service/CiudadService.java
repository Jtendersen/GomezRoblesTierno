package ar.edu.utn.desi.hogarya.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.desi.hogarya.model.Ciudad;
import ar.edu.utn.desi.hogarya.repository.CiudadRepository;

@Service
public class CiudadService {

    @Autowired
    private CiudadRepository ciudadRepository;

    public List<Ciudad> listarTodas() {
        return ciudadRepository.findAll();
    }
}
