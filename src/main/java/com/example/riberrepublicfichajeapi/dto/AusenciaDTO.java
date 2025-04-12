package com.example.riberrepublicfichajeapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AusenciaDTO {
    private LocalDate fecha;
    private String motivo;
    private boolean justificada;
    private String detalles;
    private LocalDateTime tiempoRegistrado;

}
