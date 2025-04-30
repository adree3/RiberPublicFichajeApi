package com.example.riberrepublicfichajeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDTO {
    private String dia;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

}
