package com.example.riberpublicfichajeapi.dto.usuario;

import com.example.riberpublicfichajeapi.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String token;
    private int id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
    private Usuario.Rol rol;
    private Usuario.Estado estado;
}
