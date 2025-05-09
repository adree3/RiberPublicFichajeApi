package com.example.riberrepublicfichajeapi.model;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ausencias")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Ausencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Motivo motivo = Motivo.falta_injustificada;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.vacio;

    private boolean justificada;

    private String detalles;

    @Column(nullable = false, updatable = false)
    private LocalDateTime tiempoRegistrado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Usuario usuario;

    public enum Motivo {
        retraso, permiso, vacaciones, enfermedad, falta_injustificada, otro
    }
    public enum Estado {
        vacio, pendiente, aceptada, rechazada
    }

    public Ausencia() {
    }

    public Ausencia(Integer id, LocalDate fecha, Motivo motivo, boolean justificada, String detalles, LocalDateTime tiempoRegistrado, Usuario usuario) {
        this.id = id;
        this.fecha = fecha;
        this.motivo = motivo;
        this.justificada = justificada;
        this.detalles = detalles;
        this.tiempoRegistrado = tiempoRegistrado;
        this.usuario = usuario;
    }
}
