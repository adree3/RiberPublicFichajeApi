package com.example.riberpublicfichajeapi.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CambiarContrasenaDTO {
    private String contrasenaActual;
    private String nuevaContrasena;
}
