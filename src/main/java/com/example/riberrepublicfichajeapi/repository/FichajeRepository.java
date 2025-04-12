package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {
}
