package net.hibernate.additional.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long user_id;
    private String userName;
    private TaskDTO task;
}
