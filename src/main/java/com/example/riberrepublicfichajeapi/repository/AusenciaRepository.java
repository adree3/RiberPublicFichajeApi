package com.example.riberrepublicfichajeapi.repository;


import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, Integer> {
    boolean existsByUsuarioIdAndFecha(int usuarioId, LocalDate fecha);
}
