package net.hibernate.additional.command;

import lombok.Data;
import net.hibernate.additional.dto.TaskDTO;
@Data
public class CommentCommandDTO {
    private Long id;
    private TaskCommandDTO task;
    private String comment;
    private UserCommandDTO user;
}
