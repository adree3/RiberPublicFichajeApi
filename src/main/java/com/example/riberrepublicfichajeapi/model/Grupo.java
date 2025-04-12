package com.example.riberrepublicfichajeapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private int faltasTotales;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios;

    public Grupo() {
    }

    public Grupo(Integer id, String nombre, int faltasTotales, List<Usuario> usuarios, List<Horario> horarios) {
        this.id = id;
        this.nombre = nombre;
        this.faltasTotales = faltasTotales;
        this.usuarios = usuarios;
        this.horarios = horarios;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFaltasTotales() {
        return faltasTotales;
    }

    public void setFaltasTotales(int faltasTotales) {
        this.faltasTotales = faltasTotales;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}
