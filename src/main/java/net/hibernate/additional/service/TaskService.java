package net.hibernate.additional.service;

import net.hibernate.additional.command.TaskCommandDTO;
import net.hibernate.additional.command.mapper.TaskCommandDtoEntityMapper;
import net.hibernate.additional.dto.TaskDTO;
import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.exception.NoPermissionException;
import net.hibernate.additional.mapper.TaskEntityDtoMapper;
import net.hibernate.additional.object.SessionObject;
import net.hibernate.additional.model.TagEntity;
import net.hibernate.additional.model.TaskEntity;
import net.hibernate.additional.model.UserEntity;
import net.hibernate.additional.object.TaskStatus;
import net.hibernate.additional.repository.SessionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Order;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.testcontainers.containers.PostgreSQLContainer;

import java.util.*;
import java.util.Date;


public class TaskService {
    /*static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3")
            .withUsername("anton")
            .withPassword("anton")
            .withReuse(true)
            .withDatabaseName("postgres");*/
    private Logger logger = null;
    private volatile SessionRepository sessionRepoHelper;

    public TaskService() {

        logger = LoggerFactory.getLogger(TaskService.class);
    }

    public TaskService(SessionRepository sessionRepoHelper) {
        this.sessionRepoHelper = sessionRepoHelper;
        logger = LoggerFactory.getLogger(TaskService.class);
    }

    public List<TaskDTO> listAllTasks(SessionObject sessionObject, Integer pageNumber, Integer pageSize) throws AuthenticationException {
        TaskEntityDtoMapper taskMapper = TaskEntityDtoMapper.INSTANCE;
        List<TaskEntity> taskEntities = null;
        List<TaskDTO> dtoList = new ArrayList<>();
        if (pageSize == null) {
            pageSize = 3;
        }
        if (pageSize > 50) {
            pageSize = 50;
        }
        if (pageNumber == null) {
            pageNumber = 0;
        }
        UserEntity userEntity = (new UserRegistrationService(sessionRepoHelper))
                .getUserEntity(sessionObject.getName(), sessionObject.getPassword());
        if (userEntity == null) {
            throw new AuthenticationException("no such user=" + sessionObject.getName() + " password=" + sessionObject.getPassword());
        }
        String userName = userEntity.getUserName();

        try (Session session = sessionRepoHelper.getSession().openSession()) {

            Query<TaskEntity> tasks = null;

            if (userName == null || userName.equals("ADMIN") || userName.isEmpty() || userName.equals("Unknown")) {

                tasks = session.createQuery("from TaskEntity ", TaskEntity.class);
            } else {
                tasks = session.createQuery("from TaskEntity where user= :userN", TaskEntity.class);
                System.out.println("userName=" + userName);
                tasks.setParameter("userN", userEntity);
            }
            tasks.setOrder(Order.asc(TaskEntity.class, "taskId"));
            tasks.setFirstResult((pageNumber) * pageSize);
            tasks.setMaxResults(pageSize);
            taskEntities = tasks.list();

            for (TaskEntity taskEntity : taskEntities) {
                if (taskEntity.getStatus().equals(TaskStatus.IN_PROGRESS) && taskEntity.getEndDate() != null) {
                    checkExpired(taskEntity);
                }
                TaskDTO task = taskMapper.toDTO(taskEntity);
                dtoList.add(task);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return dtoList;
    }

    private void checkExpired(TaskEntity taskEntity) {

        if (taskEntity.getEndDate().before(new Date())) {
            taskEntity.setStatus(TaskStatus.EXPIRED);

        }
    }

    public boolean editTask(TaskCommandDTO commandDTO, SessionObject sessionObject) throws AuthenticationException, NoPermissionException {
        TaskCommandDtoEntityMapper commandToEntityMapper = TaskCommandDtoEntityMapper.INSTANCE;

        UserDTO userDTO = getAuthenticatedUser(sessionObject);
        if (!userDTO.getUserName().equals("ADMIN")) {
            throw new NoPermissionException("user name= " + sessionObject.getName());
        }
        Date dateOfCreation = null;

        Boolean hasChanged = false;
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction = session.beginTransaction();
            TaskEntity taskEntity = commandToEntityMapper.toModel(commandDTO);
            TaskEntity taskTemporary = new TaskEntity();
            Long taskId = taskEntity.getTaskId();
            session.load(taskTemporary, taskId);
            dateOfCreation = taskTemporary.getCreateDate();
            UserEntity userEntity = taskTemporary.getUser();
            taskEntity.setCreateDate(dateOfCreation);
            taskEntity.setUser(userEntity);
            session.merge(taskEntity);
            if (taskId != null) {
                hasChanged = true;
            }
            hasChanged = true;
            transaction.commit();
        } catch (ConstraintViolationException e) {
            logger.info("attempt to insert new duplicate key into the table");
        }
        return hasChanged;
    }

    public TaskDTO createTask(TaskCommandDTO commandDTO, SessionObject sessionObject) throws AuthenticationException, NoPermissionException {
        TaskCommandDtoEntityMapper commandToEntityMapper = TaskCommandDtoEntityMapper.INSTANCE;
        UserDTO userDTO = getAuthenticatedUser(sessionObject);
        if (!userDTO.getUserName().equals("ADMIN")) {
            throw new NoPermissionException();
        }
        TaskEntity taskEntity = commandToEntityMapper.toModel(commandDTO);
        TaskEntity taskEntityResponse;
        TaskDTO taskDTO = null;
        UserRegistrationService userRegistrationService = new UserRegistrationService(sessionRepoHelper);
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction = session.beginTransaction();
            UserEntity userEntity = taskEntity.getUser();
            UserEntity userRegisteredEntity = null;
            if (userEntity != null) {
                userRegisteredEntity = userRegistrationService.getUserEntity(userEntity.getUserName(), null);
                session.persist(userEntity);
            }
            taskEntityResponse = (TaskEntity) session.merge(taskEntity);
            if (taskEntityResponse == null) {
                return null;
            }
            session.persist(taskEntityResponse);
            session.flush();
            session.refresh(taskEntityResponse);
            TaskEntityDtoMapper commandEntityMapper = TaskEntityDtoMapper.INSTANCE;
            taskDTO = commandEntityMapper.toDTO(taskEntityResponse);
            transaction.commit();
        }

        return taskDTO;
    }

    public boolean deleteTask(TaskCommandDTO commandDTO, SessionObject sessionObject) throws AuthenticationException, NoPermissionException {
        TaskCommandDtoEntityMapper commandToEntityMapper = TaskCommandDtoEntityMapper.INSTANCE;
        UserDTO userDTO = getAuthenticatedUser(sessionObject);
        if (!userDTO.getUserName().equals("ADMIN")) {
            throw new NoPermissionException();
        }
        TaskEntity taskEntity = commandToEntityMapper.toModel(commandDTO);
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(taskEntity);
            transaction.commit();
        }
        return true;
    }

    public int getAllCount(SessionObject sessionObject) throws AuthenticationException, NoPermissionException {
        UserDTO userDTO = getAuthenticatedUser(sessionObject);

        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Query<Long> query1 = null;
            if (!userDTO.getUserName().equals("ADMIN")) {
                query1 = session.createQuery("select count(*) from TaskEntity where name = :strName", Long.class);
                query1.setParameter("strName", userDTO.getUserName());
            } else {
                query1 = session.createQuery("select count(*) from TaskEntity ", Long.class);
            }
            Integer i = query1.list().get(0).intValue();
            return i;
        }

    }

    public Long getIdOfTag(String tagStr) {
        logger.info("getIdOfTag processing of string tagStr=" + tagStr);
        TagEntity singleTagId = null;
        Long l = null;
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            NativeQuery<Long> lon = session.createNativeQuery("select tag_id from tags where str=:tagStr", Long.class);
            lon.setParameter("tagStr", tagStr);
            l = lon.getSingleResultOrNull();
        }
        if (singleTagId != null) {
            logger.info("for tag=" + tagStr + " found number=" + singleTagId);
        } else {
            logger.info("singleTagId=" + singleTagId);
        }
        return l;
    }

    public UserDTO getAuthenticatedUser(SessionObject sessionObject) throws AuthenticationException, NoPermissionException {
        UserDTO userDTO = (new UserRegistrationService(sessionRepoHelper)).getUserDTO(sessionObject.getName(), sessionObject.getPassword());
        if (userDTO == null) {
            throw new AuthenticationException("Get authenticated user");
        }
        return userDTO;
    }
}
