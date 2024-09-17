package net.hibernate.additional.mapper;

import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface UserEntityDtoMapper {
    UserEntityDtoMapper INSTANCE= Mappers.getMapper(UserEntityDtoMapper.class);
    UserDTO toDTO(UserEntity tag);
    UserEntity toModel(UserDTO tagDTO);
}
