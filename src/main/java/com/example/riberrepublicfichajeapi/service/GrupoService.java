package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
import com.example.riberrepublicfichajeapi.dto.GrupoDTO;
import com.example.riberrepublicfichajeapi.mapper.GrupoMapper;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.repository.GrupoRepository;
import com.example.riberrepublicfichajeapi.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final HorarioRepository horarioRepository;
    private final GrupoMapper grupoMapper;

    public GrupoService(GrupoRepository grupoRepository, HorarioRepository horarioRepository, GrupoMapper grupoMapper) {
        this.grupoRepository = grupoRepository;
        this.horarioRepository = horarioRepository;
        this.grupoMapper = grupoMapper;
    }

    public void crearGrupo(Grupo grupo) {
        grupoRepository.save(grupo);
    }

    public Grupo obtenerGrupoPorId(int id) {
        return grupoRepository.findById(id).orElse(null);
    }

    public Horario obtenerHorarioPorGrupoYDia(Integer grupoId, Horario.Dia dia) {
        return horarioRepository.findByGrupoIdAndDia(grupoId, dia);
    }

    public GrupoDTO editarGrupo (int id, GrupoDTO grupoDTO) {
        Grupo grupoExistente = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        grupoExistente.setNombre(grupoDTO.getNombre());
        grupoExistente.setFaltasTotales(grupoDTO.getFaltasTotales());

        Grupo grupoActualizado = grupoRepository.save(grupoExistente);
        return grupoMapper.toDTO(grupoActualizado);
    }

    public void eliminarGrupo (int id) {
        if (grupoRepository.existsById(id)) {
            grupoRepository.deleteById(id);
        }else{
            throw new RuntimeException("Grupo no encontrado");
        }
    }

    public List<Grupo> getGrupos() {
        return grupoRepository.findAll();
    }
}
