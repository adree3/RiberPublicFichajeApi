package com.example.riberrepublicfichajeapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
public class HorarioDTO {
    private String dia;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

}
