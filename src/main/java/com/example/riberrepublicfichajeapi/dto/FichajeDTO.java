package com.example.riberrepublicfichajeapi.dto;

import com.example.riberrepublicfichajeapi.dto.usuario.UsuarioFichajeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class FichajeDTO {
    private Integer id;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private String ubicacion;
    private boolean nfcUsado;
    private UsuarioFichajeDTO usuario;

    public FichajeDTO(Integer id,
                      LocalDateTime fechaHoraEntrada,
                      LocalDateTime fechaHoraSalida,
                      String ubicacion,
                      boolean nfcUsado,
                      UsuarioFichajeDTO usuario) {
        this.id = id;
        this.fechaHoraEntrada = fechaHoraEntrada;
        this.fechaHoraSalida = fechaHoraSalida;
        this.ubicacion = ubicacion;
        this.nfcUsado = nfcUsado;
        this.usuario = usuario;
    }
}
