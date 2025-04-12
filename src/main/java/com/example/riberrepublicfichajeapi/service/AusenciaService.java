package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.AusenciaDTO;
import com.example.riberrepublicfichajeapi.mapper.AusenciaMapper;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.repository.AusenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final AusenciaMapper ausenciaMapper;

    public AusenciaService(AusenciaRepository ausenciaRepository, AusenciaMapper ausenciaMapper) {
        this.ausenciaRepository = ausenciaRepository;
        this.ausenciaMapper = ausenciaMapper;
    }

    public AusenciaDTO crearAusencia(AusenciaDTO ausenciaDTO) {
        Ausencia ausencia = ausenciaMapper.toEntity(ausenciaDTO);
        ausenciaRepository.save(ausencia);
        return ausenciaMapper.toDTO(ausencia);
    }

    public void crearAusencia(Ausencia ausencia) {
        ausenciaRepository.save(ausencia);
    }

    public AusenciaDTO editarAusencia(int id, AusenciaDTO ausenciaDTO) {
        Ausencia ausenciaExistente = ausenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ausencia no encontrada"));
        ausenciaExistente.setFecha(ausenciaDTO.getFecha());
        ausenciaExistente.setMotivo(Ausencia.Motivo.valueOf(ausenciaDTO.getMotivo()));
        ausenciaExistente.setJustificada(ausenciaDTO.isJustificada());
        ausenciaExistente.setDetalles(ausenciaDTO.getDetalles());

        Ausencia ausenciaActualizada=  ausenciaRepository.save(ausenciaExistente);
        return ausenciaMapper.toDTO(ausenciaActualizada);
    }

    public void eliminarAusencia(int id) {
        if (ausenciaRepository.existsById(id)) {
            ausenciaRepository.deleteById(id);
        }else {
            throw new RuntimeException("Ausencia no encontrada");
        }
    }

    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAll();

    }
}
