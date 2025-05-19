package com.example.riberpublicfichajeapi.repository;


import com.example.riberpublicfichajeapi.model.Ausencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, Integer> {
    boolean existsByUsuarioIdAndFecha(int usuarioId, LocalDate fecha);
}
