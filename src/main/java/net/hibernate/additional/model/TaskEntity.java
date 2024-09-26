package net.hibernate.additional.model;

import jakarta.persistence.*;
import lombok.*;
import net.hibernate.additional.object.TaskStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
//@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @Column(name = "task_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;
    private String name;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Column(name = "task_title")
    private String title;
    @OneToOne
    private UserEntity user;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)

    @JoinTable(name = "tags_tasks",
            joinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "tag_id"))

    private Set<TagEntity> tag;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY, mappedBy = "task")
    @Column(name = "task_comments")
    @ToString.Exclude
    private List<CommentEntity> comments;

    public void addTag(TagEntity tagEntity) {
        this.tag.add(tagEntity);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TaskEntity)) return false;
        final TaskEntity other = (TaskEntity) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$task_id = this.getTaskId();
        final Object other$task_id = other.getTaskId();
        if (this$task_id == null ? other$task_id != null : !this$task_id.equals(other$task_id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$createDate = this.getCreateDate();
        final Object other$createDate = other.getCreateDate();
        if (this$createDate == null ? other$createDate != null : !this$createDate.equals(other$createDate))
            return false;
        final Object this$startDate = this.getStartDate();
        final Object other$startDate = other.getStartDate();
        if (this$startDate == null ? other$startDate != null : !this$startDate.equals(other$startDate)) return false;
        final Object this$endDate = this.getEndDate();
        final Object other$endDate = other.getEndDate();
        if (this$endDate == null ? other$endDate != null : !this$endDate.equals(other$endDate)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.toString().equals(other$status.toString())) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TaskEntity;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $task_id = this.getTaskId();
        result = result * PRIME + ($task_id == null ? 43 : $task_id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $createDate = this.getCreateDate();
        result = result * PRIME + ($createDate == null ? 43 : $createDate.hashCode());
        final Object $startDate = this.getStartDate();
        result = result * PRIME + ($startDate == null ? 43 : $startDate.hashCode());
        final Object $endDate = this.getEndDate();
        result = result * PRIME + ($endDate == null ? 43 : $endDate.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.toString().hashCode());
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());

        return result;
    }


}
