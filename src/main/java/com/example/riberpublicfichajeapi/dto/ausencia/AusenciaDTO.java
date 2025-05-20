package com.example.riberpublicfichajeapi.dto.ausencia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AusenciaDTO {
    private LocalDate fecha;
    private String motivo;
    private boolean justificada;
    private String detalles;
    private LocalDateTime tiempoRegistrado;
}
