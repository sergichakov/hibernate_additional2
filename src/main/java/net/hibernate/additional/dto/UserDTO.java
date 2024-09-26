package net.hibernate.additional.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String userName;
    private TaskDTO task;
}
