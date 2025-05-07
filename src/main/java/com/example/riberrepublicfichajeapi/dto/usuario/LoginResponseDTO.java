package com.example.riberrepublicfichajeapi.dto.usuario;

import com.example.riberrepublicfichajeapi.model.Usuario;
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
    private Usuario usuario;
}
