package com.example.riberrepublicfichajeapi.mapper;

import com.example.riberrepublicfichajeapi.dto.grupo.GrupoDTO;
import com.example.riberrepublicfichajeapi.model.Grupo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GrupoMapper {
    GrupoMapper INSTANCE = Mappers.getMapper(GrupoMapper.class);

    GrupoDTO toDTO(Grupo grupo);

    @Mapping(target = "id", ignore = true)
    Grupo toEntity(GrupoDTO grupoDTO);
}
