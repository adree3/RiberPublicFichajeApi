package com.example.riberrepublicfichajeapi.mapper;

import com.example.riberrepublicfichajeapi.dto.HorarioDTO;
import com.example.riberrepublicfichajeapi.model.Horario;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HorarioMapper {
    HorarioMapper INSTANCE = Mappers.getMapper(HorarioMapper.class);

    HorarioDTO toDTO(Horario horario);

    @Mapping(target = "id", ignore = true)
    Horario toEntity(HorarioDTO horarioDTO);
}
