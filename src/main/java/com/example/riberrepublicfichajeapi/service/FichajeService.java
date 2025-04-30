package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.mapper.FichajeMapper;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.FichajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final FichajeMapper fichajeMapper;

    public FichajeService(FichajeRepository fichajeRepository, FichajeMapper fichajeMapper) {
        this.fichajeRepository = fichajeRepository;
        this.fichajeMapper = fichajeMapper;
    }

    public List<Fichaje> getFichajes() {
        return fichajeRepository.findAll();
    }

    public List<Fichaje> getFichajesPorUsuario(Usuario usuario) {
        return fichajeRepository.findFichajesByUsuario(usuario);
    }

    public Optional<Fichaje> getFichajeById(int id) {
        return fichajeRepository.findById(id);
    }

    public Fichaje crearFichaje(Fichaje fichaje) {
        fichajeRepository.save(fichaje);
        return fichaje;
    }



    public void eliminarFichaje(int id) {
        if (fichajeRepository.existsById(id)){
            fichajeRepository.deleteById(id);
        }else {
            throw new RuntimeException("Fichaje no encontrado");
        }
    }


}
