package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.dto.grupo.CrearActualizarGrupoDTO;
import com.example.riberpublicfichajeapi.model.Grupo;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.repository.GrupoRepository;
import com.example.riberpublicfichajeapi.repository.UsuarioRepository;
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

    /**
     * Crea un grupo con el nombre recibido y asigna los usuarios recibidos al grupo creado.
     *
     * @param crearActualizarGrupoDTO dto con el nombre y la lista de usuariosIds
     * @return devuelve el grupo creado
     */
    public Grupo crearGrupo(CrearActualizarGrupoDTO crearActualizarGrupoDTO) {
        // Creo un grupo y le asigno el nombre y el numFaltas
        Grupo grupo = new Grupo();
        grupo.setNombre(crearActualizarGrupoDTO.getNombre().trim());
        grupo.setFaltasTotales(0);
        final Grupo nuevoGrupo = grupoRepository.save(grupo);

        // Si hay usuariosIds asignados al grupo, convierto esos ids a usuarios y les asigno el grupo creado
        if (!crearActualizarGrupoDTO.getUsuariosIds().isEmpty()) {
            List<Usuario> usuarios = usuarioRepository.findAllById(crearActualizarGrupoDTO.getUsuariosIds());
            usuarios.forEach(u -> u.setGrupo(nuevoGrupo));
            usuarioRepository.saveAll(usuarios);
        }
        return nuevoGrupo;
    }

    /**
     * Edita un grupo, el nombre y añade o elimina los usuarios para ese grupo.
     * @param grupoId identificador del grupo
     * @param actualizarGrupoDTO nombre y lista de usuarios.
     * @return devuelve el grupo editado
     */
    public Grupo actualizarGrupo(int grupoId, CrearActualizarGrupoDTO actualizarGrupoDTO) {
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

    /**
     * Elimina un grupo por su id.
     *
     * @param grupoId identificador del grupo
     */
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
