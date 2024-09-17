package net.hibernate.additional.mapper;

import net.hibernate.additional.dto.TaskDTO;
import net.hibernate.additional.model.TaskEntity;
import org.mapstruct.Mapper;
import net.hibernate.additional.dto.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="default",uses={TagEntityDtoMapper.class,TagEntitySetDtoSetMapper.class})
        //CommentEntityDtoMapper.class,CommentEntitySetDtoSetMapper.class})
public interface TaskEntityDtoMapper {
    TaskEntityDtoMapper INSTANCE= Mappers.getMapper(TaskEntityDtoMapper.class);
    TaskDTO toDTO(TaskEntity task);
    TaskEntity toModel(TaskDTO taskDTO);
}
