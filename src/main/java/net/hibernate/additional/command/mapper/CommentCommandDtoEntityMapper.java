package net.hibernate.additional.command.mapper;

//import CommentCommandDTO;
import net.hibernate.additional.command.CommentCommandDTO;
import net.hibernate.additional.model.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="default")
public interface CommentCommandDtoEntityMapper {
    CommentCommandDtoEntityMapper INSTANCE= Mappers.getMapper(CommentCommandDtoEntityMapper.class);
    @Mapping(target = "task", ignore = true)
    CommentCommandDTO toDTO(CommentEntity tag);//delete this string эта строка не нужна
    @Mapping(target = "task", ignore = true)
    CommentEntity toModel(CommentCommandDTO tagDTO);
}
