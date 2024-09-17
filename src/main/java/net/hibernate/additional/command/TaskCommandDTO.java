package net.hibernate.additional.command;

import lombok.*;
import net.hibernate.additional.object.TaskStatus;

import java.util.Date;
import java.util.Set;
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class TaskCommandDTO {
    private Long task_id;
    private String name;
    private Date createDate;
    private Date startDate;
    private Date endDate;
    private TaskStatus status;
    private UserCommandDTO user;
    private String title;
    private Set<TagCommandDTO> tag;//TagEntity
}
