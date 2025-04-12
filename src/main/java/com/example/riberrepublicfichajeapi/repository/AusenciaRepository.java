package com.example.riberrepublicfichajeapi.repository;


import com.example.riberrepublicfichajeapi.model.Ausencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AusenciaRepository extends JpaRepository<Ausencia, Integer> {
}
