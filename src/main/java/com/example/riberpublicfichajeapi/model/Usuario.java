package com.example.riberpublicfichajeapi.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "usuarios")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 25)
    private String nombre;

    @Column(nullable = false, length = 30)
    private String apellido1;

    @Column(length = 30)
    private String apellido2;

    @Column(nullable = false, unique = true, length = 40)
    private String email;

    @Column(length = 255)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.empleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.activo;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    @JsonIdentityReference(alwaysAsId = true)
    private Grupo grupo;

    public enum Rol {
        empleado, jefe
    }

    public enum Estado {
        activo, inactivo
    }

    public Usuario() {
    }

    public Usuario(Integer id, String nombre, String apellido1, String apellido2, String email, String contrasena, Rol rol, Estado estado, Grupo grupo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.email = email;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.grupo = grupo;
    }

}

