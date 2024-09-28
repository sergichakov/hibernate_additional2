package net.hibernate.additional.command.mapper;

import net.hibernate.additional.command.TagCommandDTO;
import net.hibernate.additional.command.UserCommandDTO;
import net.hibernate.additional.model.TagEntity;
import net.hibernate.additional.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCommandDtoEntityMapper {
    UserCommandDtoEntityMapper INSTANCE= Mappers.getMapper(UserCommandDtoEntityMapper.class);
    UserCommandDTO toDTO(UserEntity tag);
    UserEntity toModel(UserCommandDTO tagDTO);
}