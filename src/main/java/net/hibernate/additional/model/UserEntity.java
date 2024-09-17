package net.hibernate.additional.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.BasicType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
@Setter
@Getter
@Entity
@Table(name="users")

public class  UserEntity implements Serializable{//UserType<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Long user_id;
    @Column(name="user_name",length=100)
    private String userName;
    @Column(name="password",length=100)
    @ToString.Exclude
    private String password;
    @OneToOne(fetch=FetchType.LAZY)
    private TaskEntity task;
}
