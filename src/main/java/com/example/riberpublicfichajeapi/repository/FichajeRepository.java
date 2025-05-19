package com.example.riberpublicfichajeapi.repository;

import com.example.riberpublicfichajeapi.model.Fichaje;
import com.example.riberpublicfichajeapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {
    List<Fichaje> findFichajesByUsuario(Usuario usuario);

    Optional<Fichaje> findFirstByUsuarioAndFechaHoraEntradaBetween(Usuario usuario, LocalDateTime inicioHoy, LocalDateTime inicioManana);

    Optional<Fichaje> findFirstByUsuarioAndFechaHoraEntradaBetweenAndFechaHoraSalidaIsNull(Usuario usuario, LocalDateTime desde, LocalDateTime hasta);

    List<Fichaje> findAllByUsuarioAndFechaHoraEntradaBetween(Usuario usuario, LocalDateTime inicioHoy, LocalDateTime inicioManana);

    boolean existsByUsuarioIdAndFechaHoraEntradaBetween(int usuarioId, LocalDateTime inicioDia, LocalDateTime finDia);
}
