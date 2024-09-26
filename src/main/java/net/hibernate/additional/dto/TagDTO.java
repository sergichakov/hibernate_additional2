package net.hibernate.additional.dto;

import lombok.Data;

import java.util.Set;
@Data
public class TagDTO {
    private Long tagId;
    private String str;
    private Set<TaskDTO> task;
}
