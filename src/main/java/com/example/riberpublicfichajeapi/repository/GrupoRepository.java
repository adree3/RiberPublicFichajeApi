package com.example.riberpublicfichajeapi.repository;

import com.example.riberpublicfichajeapi.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    Optional<Grupo> findByNombre(String nombre);
}
