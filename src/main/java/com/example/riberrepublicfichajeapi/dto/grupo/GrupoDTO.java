package com.example.riberrepublicfichajeapi.dto.grupo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GrupoDTO {
    private String nombre;
    private int faltasTotales;

}
