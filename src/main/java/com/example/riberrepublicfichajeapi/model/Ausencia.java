package com.example.riberrepublicfichajeapi.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ausencias")
public class Ausencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Motivo motivo = Motivo.falta_injustificada;

    private boolean justificada;

    private String detalles;

    @Column(nullable = false, updatable = false)
    private LocalDateTime tiempoRegistrado;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public enum Motivo {
     retraso, permiso, vacaciones, enfermedad, falta_injustificada, otro
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public void setMotivo(Motivo motivo) {
        this.motivo = motivo;
    }

    public boolean isJustificada() {
        return justificada;
    }

    public void setJustificada(boolean justificada) {
        this.justificada = justificada;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getTiempoRegistrado() {
        return tiempoRegistrado;
    }

    public void setTiempoRegistrado(LocalDateTime tiempoRegistrado) {
        this.tiempoRegistrado = tiempoRegistrado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
