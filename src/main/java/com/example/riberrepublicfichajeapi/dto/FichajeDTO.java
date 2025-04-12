package com.example.riberrepublicfichajeapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class FichajeDTO {
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private String ubicacion;
    private boolean nfcUsado;

}
