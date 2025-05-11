package com.example.riberrepublicfichajeapi.dto.horario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HorarioHoyDTO {
    private String horaEntrada;
    private String horaSalida;
    private String horasEstimadas;
}
