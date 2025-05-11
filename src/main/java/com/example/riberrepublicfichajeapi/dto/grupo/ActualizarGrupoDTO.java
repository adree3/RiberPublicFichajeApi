package com.example.riberrepublicfichajeapi.dto.grupo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarGrupoDTO {
    private String nombre;
    private List<Integer> usuariosIds;
}
