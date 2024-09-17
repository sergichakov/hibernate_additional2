package net.hibernate.additional.command;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import net.hibernate.additional.command.CommentCommandDTO;
import net.hibernate.additional.dto.TaskDTO;
import net.hibernate.additional.model.CommentEntity;
@Data
public class UserCommandDTO {
    private Long user_id;
    private String userName;
    @ToString.Exclude
    private String password;
    @ToString.Exclude
    private TaskDTO task;
}
