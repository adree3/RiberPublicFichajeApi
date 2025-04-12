package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
}
