package com.example.riberrepublicfichajeapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "horarios")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dia dia;

    @Column(nullable = false)
    private LocalTime horaEntrada;

    @Column(nullable = false)
    private LocalTime horaSalida;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Grupo grupo;

    public enum Dia {
        lunes, martes, miercoles, jueves, viernes, sabado, domingo
    }

    public Horario() {
    }

    public Horario(Integer id, Dia dia, LocalTime horaEntrada, LocalTime horaSalida, Grupo grupo) {
        this.id = id;
        this.dia = dia;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.grupo = grupo;
    }

}

