package com.example.riberpublicfichajeapi.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
    private String rol;
    private String estado;
    private String contrasena;

}
