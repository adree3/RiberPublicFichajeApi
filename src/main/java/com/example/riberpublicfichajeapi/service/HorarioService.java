package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.dto.horario.EditarHorarioDTO;
import com.example.riberpublicfichajeapi.dto.horario.HorarioDTO;
import com.example.riberpublicfichajeapi.dto.horario.HorarioHoyDTO;
import com.example.riberpublicfichajeapi.model.Grupo;
import com.example.riberpublicfichajeapi.model.Horario;
import com.example.riberpublicfichajeapi.repository.GrupoRepository;
import com.example.riberpublicfichajeapi.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;


    public HorarioService(HorarioRepository horarioRepository, GrupoRepository grupoRepository) {
        this.horarioRepository = horarioRepository;
        this.grupoRepository = grupoRepository;
    }

    /**
     * Obtiene todos los horarios.
     * @return lista de horarios
     */
    public List<Horario> getHorarios() {
        return horarioRepository.findAll();
    }

    /**
     * Obtiene los horarios por un grupo.
     * @param grupoId identificador del grupo
     * @return la lista de horarios
     */
    public List<Horario> getHorariosPorGrupo(int grupoId) {
        return horarioRepository.findByGrupoId(grupoId);
    }

    /**
     * Crea un horario por los parametros recibidos y añadiendole el grupo, sino le asigna uno por defecto.
     *
     * @param idGrupo grupo al que se le asigna
     * @param horarioDTO parametros para crear el horario
     * @return devuelve el horario creado
     */
    public Horario crearHorario(int idGrupo, HorarioDTO horarioDTO) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElse(grupoRepository.findByNombre("Sin Asignar")
                        .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado")));

        horarioRepository.findFirstByGrupoIdAndDia(idGrupo, Horario.Dia.valueOf(horarioDTO.getDia()))
                .ifPresent(h -> {
                    throw new IllegalStateException("Ya existe un horario para ese día");
                });
        Horario.Dia diaEnum = Horario.Dia.valueOf(horarioDTO.getDia());
        Horario horario = new Horario();
        horario.setGrupo(grupo);
        horario.setDia(diaEnum);
        horario.setHoraEntrada(horarioDTO.getHoraEntrada());
        horario.setHoraSalida(horarioDTO.getHoraSalida());

        return horarioRepository.save(horario);
    }

    /**
     * Convierte el horario en horarioDTO
     * @param horario objeto a convertir
     * @return devuelve el horarioDTO
     */
    public HorarioHoyDTO toDto(Horario horario) {
        Duration duracion = Duration.between(horario.getHoraEntrada(), horario.getHoraSalida());
        String horasEstimadas = String.format("%02d:%02d:%02d",
                duracion.toHours(),
                duracion.toMinutesPart(),
                duracion.toSecondsPart());
        return new HorarioHoyDTO(horario.getHoraEntrada().toString(), horario.getHoraSalida().toString(), horasEstimadas);
    }

    /**
     * Actualiza el día, fecha entrada, fecha salida y grupo de un horario
     *
     * @param id id del horario a modificar
     * @param editarHorarioDTO dto con el día, fecha e idgrupo para modificar
     * @return devuelve el horario modificado
     */
    public Horario editarHorario(int id, EditarHorarioDTO editarHorarioDTO) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado"));

        Horario.Dia nuevoDia = Horario.Dia.valueOf(editarHorarioDTO.getDia());
        Integer nuevoGrupoId = editarHorarioDTO.getGrupoId();

        // comprobar si ya existe un horario para ese dia y grupo, sin contar el actual
        horarioRepository.findFirstByGrupoIdAndDia(nuevoGrupoId, nuevoDia)
                .filter(h -> !h.getId().equals(id))
                .ifPresent(h -> {
                    throw new IllegalStateException(
                            "Ya existe un horario para " + nuevoDia + " en ese grupo");
                });

        horario.setDia(nuevoDia);
        horario.setHoraEntrada(LocalTime.parse(editarHorarioDTO.getHoraEntrada()));
        horario.setHoraSalida(LocalTime.parse(editarHorarioDTO.getHoraSalida()));
        Grupo grupo = grupoRepository.findById(nuevoGrupoId)
                .orElse(grupoRepository.findByNombre("Sin Asignar")
                        .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado")));

        horario.setGrupo(grupo);
        return horarioRepository.save(horario);
    }

    /**
     * Elimina un horario por su id.
     *
     * @param id identificador del horario
     */
    public void eliminarHorario(int id) {
        if (!horarioRepository.existsById(id)) {
            throw new RuntimeException("Horario no encontrado");
        }
        horarioRepository.deleteById(id);
    }
}
