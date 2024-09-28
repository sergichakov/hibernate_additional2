package net.hibernate.additional.mapper;

import net.hibernate.additional.dto.CommentDTO;
import net.hibernate.additional.model.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="default")
public interface CommentEntityDtoMapper {
    CommentEntityDtoMapper INSTANCE= Mappers.getMapper(CommentEntityDtoMapper.class);
    @Mapping(target = "task", ignore = true)
    CommentDTO toDTO(CommentEntity tag);
    CommentEntity toModel(CommentDTO tagDTO);
}
