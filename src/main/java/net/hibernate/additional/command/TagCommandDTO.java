package net.hibernate.additional.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hibernate.additional.model.TaskEntity;

import java.util.Set;
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCommandDTO {
    private Long tagId;
    private String str;
    private Set<TaskEntity> task;
}
