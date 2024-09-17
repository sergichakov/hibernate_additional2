package net.hibernate.additional.model;

import jakarta.persistence.*;
import lombok.*;
import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.model.TaskEntity;
import org.hibernate.annotations.Type;
import org.hibernate.type.BasicType;
import org.hibernate.usertype.UserType;

import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name="comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ToString.Exclude
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JoinColumn(name="task_id")
    private TaskEntity task;
    @Column (name="comment_of_task", length=512)
    private String comment;
    @Type(UserDefinedType.class)
    @Column(name="user_type")
    private UserEntity user;
    public void setTask(TaskEntity taskEntity){
        this.task=(taskEntity);
    }
}
