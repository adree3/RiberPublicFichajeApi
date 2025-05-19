package com.example.riberpublicfichajeapi.dto.horario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditarHorarioDTO {
    private String dia;
    private String horaEntrada;
    private String horaSalida;
    private Integer grupoId;
}
