package com.example.riberrepublicfichajeapi.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsuarioDTO {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
    private String rol;
    private String estado;
    private String contraseña;

}
