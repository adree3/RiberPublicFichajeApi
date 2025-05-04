package com.example.riberrepublicfichajeapi.mapper;

import com.example.riberrepublicfichajeapi.dto.usuario.UsuarioDTO;
import com.example.riberrepublicfichajeapi.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);
}
