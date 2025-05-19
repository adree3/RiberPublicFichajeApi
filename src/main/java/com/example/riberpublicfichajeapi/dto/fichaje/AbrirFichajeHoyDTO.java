package com.example.riberpublicfichajeapi.dto.fichaje;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbrirFichajeHoyDTO {
    private boolean nfcUsado;
    private String ubicacion;
}
