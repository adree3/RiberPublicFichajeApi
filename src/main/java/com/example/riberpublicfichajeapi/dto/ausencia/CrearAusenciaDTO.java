package com.example.riberpublicfichajeapi.dto.ausencia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrearAusenciaDTO {
    private LocalDate fecha;
    private String motivo;
    private String detalles;
}
