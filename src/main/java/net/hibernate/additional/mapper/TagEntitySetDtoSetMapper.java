package net.hibernate.additional.mapper;

import net.hibernate.additional.model.TaskEntity;
import net.hibernate.additional.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;
@Mapper(componentModel="default")
public interface TagEntitySetDtoSetMapper {
    TagEntitySetDtoSetMapper INSTANCE= Mappers.getMapper(TagEntitySetDtoSetMapper.class);
    @Mapping(target = "task", ignore = true)
    Set<TaskEntity> toEntitySet(Set<TaskDTO> dto);
    @Mapping(target = "task", ignore = true)
    Set<TaskDTO> toDtoSet(Set<TaskEntity> entity);
}
