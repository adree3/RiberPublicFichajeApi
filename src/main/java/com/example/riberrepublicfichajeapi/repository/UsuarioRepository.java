package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByEstado(Usuario.Estado estado);

    List<Usuario> findByGrupo(Grupo grupo);

    List<Usuario> findByGrupoId(int grupoId);
}
