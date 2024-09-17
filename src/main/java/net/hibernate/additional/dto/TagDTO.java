package net.hibernate.additional.dto;

import lombok.Data;

import java.util.Set;
@Data
public class TagDTO {
    private Long tag_id;
    private String str;
    private Set<TaskDTO> task;//=new HashSet<>();
}
