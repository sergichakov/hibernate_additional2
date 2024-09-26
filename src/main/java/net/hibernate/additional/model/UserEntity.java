package net.hibernate.additional.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Setter
@Getter
@Entity
@Table(name="users")

public class  UserEntity implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    @Column(name= "user_id")
    private Long userId;
    @Column(name="user_name",length=100)
    private String userName;
    @Column(name="password",length=100)
    @ToString.Exclude
    private String password;
    @OneToOne(fetch=FetchType.LAZY)
    private TaskEntity task;
}
