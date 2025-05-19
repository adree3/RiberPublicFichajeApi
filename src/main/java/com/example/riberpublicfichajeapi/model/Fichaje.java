package com.example.riberpublicfichajeapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "fichajes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fechaHoraEntrada;

    private LocalDateTime fechaHoraSalida;

    private String ubicacion;

    private boolean nfcUsado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Usuario usuario;

    public Fichaje() {
    }

    public Fichaje(Integer id, LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida, String ubicacion, boolean nfcUsado, Usuario usuario) {
        this.id = id;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.fechaHoraSalida = fechaHoraSalida;
        this.ubicacion = ubicacion;
        this.nfcUsado = nfcUsado;
        this.usuario = usuario;
    }

}
