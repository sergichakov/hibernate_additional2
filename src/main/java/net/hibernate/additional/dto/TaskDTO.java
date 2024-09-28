package net.hibernate.additional.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.hibernate.additional.object.TaskStatus;

import java.util.Date;
import java.util.Set;

@Setter
@Data
@Getter
public class TaskDTO {
    private Long taskId;
    private String name;
    private Date createDate;
    private Date startDate;
    private Date endDate;
    private TaskStatus status;
    private UserDTO user;
    private String title;
    private Set<TagDTO> tag;
}
