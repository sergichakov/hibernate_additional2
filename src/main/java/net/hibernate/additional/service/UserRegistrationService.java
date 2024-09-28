package net.hibernate.additional.service;

import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.mapper.UserEntityDtoMapper;
import net.hibernate.additional.model.UserEntity;
import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.repository.SessionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserRegistrationService {
    private volatile SessionRepository sessionRepoHelper = null;

    public UserRegistrationService(SessionRepository sessionRepoHelper) {
        this.sessionRepoHelper = sessionRepoHelper;
    }

    public UserEntity registerUser(String userName, String password) {
        UserEntity userEntity = null;
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction = session.beginTransaction();
            userEntity = new UserEntity();
            userEntity.setUserName(userName);
            userEntity.setPassword(password);
            session.persist(userEntity);
            transaction.commit();
            session.refresh(userEntity);
        }
        return userEntity;
    }

    public UserDTO getUserDTO(String userName, String password) throws AuthenticationException {
        UserEntity userEntity = getUserEntity(userName, password);
        UserEntityDtoMapper userEntityDtoMapper = UserEntityDtoMapper.INSTANCE;
        UserDTO userDTO = userEntityDtoMapper.toDTO(userEntity);
        return userDTO;
    }

    public UserEntity getUserEntity(String userName, String password) throws AuthenticationException {
        if (userName == null) {
            throw new AuthenticationException("Given userName is null= " + userName);
        }
        UserEntity userEntity = null;

        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Query<UserEntity> userEntityQuery = session.createQuery("from UserEntity ue  where ue.userName = :userN", UserEntity.class);
            userEntityQuery.setParameter("userN", userName);
            List<UserEntity> listEntity = userEntityQuery.list();
            if (!listEntity.isEmpty()) {
                userEntity = listEntity.get(0);
            }
            if (userEntity == null) {
                if (password == null || password.isEmpty()) {
                    return null;
                }
                userEntity = registerUser(userName, password);
                return userEntity;
            }
            if (userEntity.getPassword() == null) {
                if (password == null || password.isEmpty()) {
                    //throw new AuthenticationException("stored password and given is Null");
                } else {
                    Transaction transaction = session.beginTransaction();
                    userEntity.setPassword(password);
                    session.persist(userEntity);
                    transaction.commit();
                }
            } else {
                if (!userEntity.getPassword().equals(password)) {
                    throw new AuthenticationException("stored password Not equal to given");
                }
            }
            return userEntity;
        }
    }
}
