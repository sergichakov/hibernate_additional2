package net.hibernate.additional.command.mapper;


import net.hibernate.additional.command.CommentCommandDTO;
import net.hibernate.additional.model.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="default")
public interface CommentCommandDtoEntityMapper {
    CommentCommandDtoEntityMapper INSTANCE= Mappers.getMapper(CommentCommandDtoEntityMapper.class);
    @Mapping(target = "task", ignore = true)
    CommentCommandDTO toDTO(CommentEntity tag);
    @Mapping(target = "task", ignore = true)
    CommentEntity toModel(CommentCommandDTO tagDTO);
}
