package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.grupo.ActualizarGrupoDTO;
import com.example.riberrepublicfichajeapi.mapper.GrupoMapper;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.GrupoRepository;
import com.example.riberrepublicfichajeapi.repository.HorarioRepository;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;


    public GrupoService(GrupoRepository grupoRepository, UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Grupo> getGrupos() {
        return grupoRepository.findAll();
    }

    public Grupo obtenerGrupoPorId(int id) {
        return grupoRepository.findById(id).orElse(null);
    }

    public void crearGrupo(Grupo grupo) {
        grupoRepository.save(grupo);
    }

    public Grupo actualizarGrupo(int grupoId, ActualizarGrupoDTO actualizarGrupoDTO) {
        // se coge el grupo con el id que se recibe
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado: " + grupoId));

        // se actualiza el nombre del grupo y se guarda
        grupo.setNombre(actualizarGrupoDTO.getNombre());
        grupoRepository.save(grupo);

        // Se cogen los ids de los usuarios para coger el usuario entero de cada id y se le asigna al grupo recibido
        List<Usuario> nuevos = usuarioRepository.findAllById(actualizarGrupoDTO.getUsuariosIds());
        for (Usuario usuario: nuevos) {
            usuario.setGrupo(grupo);
        }

        // se coge el grupo por defecto
        Grupo grupoSinAsignar = grupoRepository.findByNombre("Sin Asignar")
                .orElseThrow(() -> new IllegalArgumentException("El grupo 'Sin Asignar' no existe"));

        // Se desasigna a los usuarios que no esten en la lista de usuarios recibida
        List<Usuario> usuariosActuales = usuarioRepository.findByGrupo(grupo);
        List<Integer> idsNuevos = actualizarGrupoDTO.getUsuariosIds();
        // Se comprueba si los usuariosActuales estan en la lista de usuarios nuevos
        List<Usuario> aDesasignar = usuariosActuales.stream()
                .filter(u -> !idsNuevos.contains(u.getId()))
                .collect(Collectors.toList());
        // los usuarios que no tenian los ids de idsNuevos se les asigna el grupo por defecto
        for (Usuario usuario: aDesasignar) {
            usuario.setGrupo(grupoSinAsignar);
        }

        usuarioRepository.saveAll(nuevos);
        usuarioRepository.saveAll(aDesasignar);

        return grupo;
    }

    public void eliminarGrupo(int grupoId) {
        Grupo grupoAEliminar = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado: " + grupoId));

        Grupo sinAsignar = grupoRepository.findByNombre("Sin Asignar")
                .orElseThrow(() -> new IllegalArgumentException("El grupo 'Sin Asignar' no existe"));

        // Se obtienen los usuarios del grupo que se va a eleminar y se los asigna a "Sin Asignar"
        List<Usuario> usuarios = usuarioRepository.findByGrupo(grupoAEliminar);
        usuarios.forEach(u -> u.setGrupo(sinAsignar));
        usuarioRepository.saveAll(usuarios);

        grupoRepository.delete(grupoAEliminar);
    }


}
