package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
import com.example.riberrepublicfichajeapi.mapper.FichajeMapper;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.repository.FichajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final FichajeMapper fichajeMapper;

    public FichajeService(FichajeRepository fichajeRepository, FichajeMapper fichajeMapper) {
        this.fichajeRepository = fichajeRepository;
        this.fichajeMapper = fichajeMapper;
    }

    public void crearFichaje(Fichaje fichaje) {
        fichajeRepository.save(fichaje);
    }

//    public FichajeDTO editarFichaje(FichajeDTO fichajeDTO) {
//        Fichaje fichajeExistente = fichajeRepository.findById(fichajeDTO.getId())
//                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));
//        fichajeExistente.setFechaHoraEntrada(fichajeDTO.getFechaHoraEntrada());
//        fichajeExistente.setFechaHoraSalida(fichajeDTO.getFechaHoraEntrada());
//        fichajeExistente.setUbicacion(fichajeDTO.getUbicacion());
//        fichajeExistente.setNfcUsado(fichajeDTO.isNfcUsado());
//
//        Fichaje fichajeActualizado= fichajeRepository.save(fichajeExistente);
//        return fichajeMapper.toDTO(fichajeActualizado);
//    }

    public void eliminarFichaje(int id) {
        if (fichajeRepository.existsById(id)){
            fichajeRepository.deleteById(id);
        }else {
            throw new RuntimeException("Fichaje no encontrado");
        }
    }

    public List<Fichaje> getFichajes() {
        return fichajeRepository.findAll();
    }
}
