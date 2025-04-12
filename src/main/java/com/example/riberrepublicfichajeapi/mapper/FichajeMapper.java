package com.example.riberrepublicfichajeapi.mapper;

import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FichajeMapper {
    FichajeMapper INSTANCE = Mappers.getMapper(FichajeMapper.class);

    FichajeDTO toDTO(Fichaje fichaje);

    @Mapping(target = "id", ignore = true)
    Fichaje toEntity(FichajeDTO fichajeDTO);
}
