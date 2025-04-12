package com.example.riberrepublicfichajeapi.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichajes")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isNfcUsado() {
        return nfcUsado;
    }

    public void setNfcUsado(boolean nfcUsado) {
        this.nfcUsado = nfcUsado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
