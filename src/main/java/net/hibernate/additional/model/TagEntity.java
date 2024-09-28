package net.hibernate.additional.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@ToString
@Builder
@Entity
@Table(name = "tags")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long tagId;
    @Column(name = "str", length = 100)
    private String str;
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "tag")
    private Set<TaskEntity> task;

    public void add(TaskEntity taskEntity) {
        this.task.add(taskEntity);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TagEntity)) return false;
        final TagEntity other = (TagEntity) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$tag_id = this.getTagId();
        final Object other$tag_id = other.getTagId();
        if (this$tag_id == null ? other$tag_id != null : !this$tag_id.equals(other$tag_id)) return false;
        final Object this$str = this.getStr();
        final Object other$str = other.getStr();
        if (this$str == null ? other$str != null : !this$str.equals(other$str)) return false;

        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TagEntity;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $tag_id = this.getTagId();
        result = result * PRIME + ($tag_id == null ? 43 : $tag_id.hashCode());
        final Object $str = this.getStr();
        result = result * PRIME + ($str == null ? 43 : $str.hashCode());
        return result;
    }


}
