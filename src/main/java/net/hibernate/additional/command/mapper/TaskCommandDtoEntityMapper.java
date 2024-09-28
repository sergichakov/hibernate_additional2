package net.hibernate.additional.command.mapper;

import net.hibernate.additional.command.TaskCommandDTO;import net.hibernate.additional.dto.*;

import net.hibernate.additional.model.TaskEntity;
import org.mapstruct.Mapper;
import net.hibernate.additional.command.*;
import net.hibernate.additional.command.mapper.*;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(componentModel="default",uses={
        TagCommandDtoEntityMapper.class, TagCommandDtoSetEntitySetMapper.class})

public interface TaskCommandDtoEntityMapper {
    TaskCommandDtoEntityMapper INSTANCE= Mappers.getMapper(TaskCommandDtoEntityMapper.class);

    TaskCommandDTO toDTO(TaskEntity task);
    @Mapping(ignore = true,target="createDate",source="createDate")
    TaskEntity toModel(TaskCommandDTO taskDTO);


}
