package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.HorarioDTO;
import com.example.riberrepublicfichajeapi.dto.HorarioHoyDTO;
import com.example.riberrepublicfichajeapi.mapper.HorarioMapper;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final HorarioMapper horarioMapper;

    public HorarioService(HorarioRepository horarioRepository, HorarioMapper horarioMapper) {
        this.horarioRepository = horarioRepository;
        this.horarioMapper = horarioMapper;
    }

    public Horario obtenerHorarioPorId(int id) {
        return horarioRepository.findById(id).orElse(null);
    }

    public void crearHorario(Horario horario) {
        horarioRepository.save(horario);
    }

    public HorarioHoyDTO toDto(Horario horario) {
        Duration duracion = Duration.between(horario.getHoraEntrada(), horario.getHoraSalida());
        String horasEstimadas = String.format("%02d:%02d:%02d",
                duracion.toHours(),
                duracion.toMinutesPart(),
                duracion.toSecondsPart());
        return new HorarioHoyDTO(horario.getHoraEntrada().toString(), horario.getHoraSalida().toString(), horasEstimadas);
    }

    public HorarioHoyDTO buildDefaultHorarioDTO() {
        LocalTime entrada = LocalTime.of(9, 0);
        LocalTime salida  = LocalTime.of(17, 0);
        Duration duracion = Duration.between(entrada, salida);
        String horasEstimadas = String.format("%02d:%02d:%02d",
                duracion.toHours(),
                duracion.toMinutesPart(),
                duracion.toSecondsPart());
        return new HorarioHoyDTO(entrada.toString(),salida.toString(), horasEstimadas);
    }

    public HorarioDTO editarHorario(int id, HorarioDTO horarioDTO) {
        Horario horarioExistente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        horarioExistente.setDia(Horario.Dia.valueOf(horarioDTO.getDia()));
        horarioExistente.setHoraEntrada(horarioDTO.getHoraEntrada());
        horarioExistente.setHoraSalida(horarioDTO.getHoraSalida());

        Horario horarioActualizado= horarioRepository.save(horarioExistente);
        return horarioMapper.toDTO(horarioActualizado);
    }

    public void eliminarHorario(int id) {
        if (horarioRepository.existsById(id)) {
            horarioRepository.deleteById(id);
        }else {
            throw new RuntimeException("Horario no encontrado");
        }
    }

    public List<Horario> getHorarios() {
        return horarioRepository.findAll();
    }
}
