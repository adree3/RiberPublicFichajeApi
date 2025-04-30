package com.example.riberrepublicfichajeapi.repository;

import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {
    List<Fichaje> findFichajesByUsuario(Usuario usuario);
}
