package net.hibernate.additional.command;

import lombok.Data;
import lombok.ToString;
import net.hibernate.additional.dto.TaskDTO;

@Data
public class UserCommandDTO {
    private Long userId;
    private String userName;
    @ToString.Exclude
    private String password;
    @ToString.Exclude
    private TaskDTO task;
}
