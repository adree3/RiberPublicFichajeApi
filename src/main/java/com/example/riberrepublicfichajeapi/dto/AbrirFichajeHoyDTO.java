package com.example.riberrepublicfichajeapi.dto;

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
