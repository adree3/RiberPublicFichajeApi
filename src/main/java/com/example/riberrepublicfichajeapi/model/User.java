//package com.example.riberrepublicfichajeapi.model;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "User")
//public class User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private int id;
//
//    @Column(name = "nombre")
//    private String nombre;
//
//    @Column(name = "contrasena")
//    private String contrasena;
//
//    @Column(name = "token")
//    private String token;
//
//    public User(int id, String nombre, String contrasena, String token) {
//        this.id = id;
//        this.nombre = nombre;
//        this.contrasena = contrasena;
//        this.token = token;
//    }
//
//    public User() {
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getNombre() {
//        return nombre;
//    }
//
//    public void setNombre(String nombre) {
//        this.nombre = nombre;
//    }
//
//    public String getContrasena() {
//        return contrasena;
//    }
//
//    public void setContrasena(String contrasena) {
//        this.contrasena = contrasena;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", nombre='" + nombre + '\'' +
//                ", contrasena='" + contrasena + '\'' +
//                ", token='" + token + '\'' +
//                '}';
//    }
//}
