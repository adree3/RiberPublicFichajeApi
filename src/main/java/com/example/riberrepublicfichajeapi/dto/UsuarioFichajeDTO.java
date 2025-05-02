package com.example.riberrepublicfichajeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioFichajeDTO {
    private Integer id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
}
