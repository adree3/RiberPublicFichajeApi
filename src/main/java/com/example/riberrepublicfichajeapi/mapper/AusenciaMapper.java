package com.example.riberrepublicfichajeapi.mapper;

import com.example.riberrepublicfichajeapi.dto.ausencia.AusenciaDTO;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AusenciaMapper {
    AusenciaMapper INSTANCE = Mappers.getMapper(AusenciaMapper.class);

    AusenciaDTO toDTO(Ausencia ausencia);

    @Mapping(target = "id", ignore = true)
    Ausencia toEntity(AusenciaDTO ausenciaDTO);
}
