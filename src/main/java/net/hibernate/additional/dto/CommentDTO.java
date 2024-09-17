package net.hibernate.additional.dto;

import lombok.Data;
@Data
public class CommentDTO {
    private Long id;
    private TaskDTO task;
    private String comment;
}
