package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    Horario findByGrupoIdAndDia(Integer grupoId, Horario.Dia dia);
}
