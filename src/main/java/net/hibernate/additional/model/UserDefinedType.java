package net.hibernate.additional.model;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
public class UserDefinedType implements UserType<UserEntity> {
    public static final UserDefinedType INSTANCE=new UserDefinedType();
    private UserEntity usersValue=null;
    public UserDefinedType(){
    }

    @Override
    public int getSqlType() {
        return SqlTypes.LONGVARCHAR;
    }

    @Override
    public Class returnedClass() {
        return UserEntity.class;
    }

    @Override
    public boolean equals(UserEntity x, UserEntity y) {
        return Objects.equals(x,y);
    }
    @Override
    public int hashCode(UserEntity x) {
        return 0;
    }
    @Override
    public UserEntity nullSafeGet(ResultSet resultSet, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Long result=resultSet.getLong(position);
        if (resultSet.wasNull()) {
            return null;
        }
        UserEntity users=new UserEntity();
        users.setUserId(result);
        return users;
    }
    @Override
    public void nullSafeSet(PreparedStatement st, UserEntity value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, SqlTypes.LONGVARCHAR);
        }else {
            st.setLong(index, value.getUserId());
        }
    }
    @Override
    public UserEntity deepCopy(UserEntity value) {
        return value == null ? null :
                (UserEntity)  SerializationUtils.clone(this.usersValue);
    }
    @Override
    public boolean isMutable() {
        return true;
    }
    @Override
    public Serializable disassemble(UserEntity value) {
        return deepCopy(value);
    }
    @Override
    public UserEntity assemble(Serializable cached, Object owner) {
        return deepCopy((UserEntity) cached);
    }
}