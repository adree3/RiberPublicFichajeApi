package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    List<Horario> findByGrupoIdAndDia(int grupoId, Horario.Dia dia);

    List<Horario> findByGrupoId(int grupoId);

    Optional<Horario> findFirstByGrupoIdAndDia(Integer grupoId, Horario.Dia dia);

    List<Horario> findByDia(Horario.Dia dia);
}
