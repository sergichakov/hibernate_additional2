package net.hibernate.additional.service;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import net.hibernate.additional.command.TaskCommandDTO;
import net.hibernate.additional.command.mapper.TaskCommandDtoEntityMapper;
import net.hibernate.additional.dto.TaskDTO;
import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.exception.NoPermissionException;
import net.hibernate.additional.model.CommentEntity;
import net.hibernate.additional.model.TagEntity;
import net.hibernate.additional.model.TaskEntity;
import net.hibernate.additional.model.UserEntity;
import net.hibernate.additional.object.SessionObject;
import net.hibernate.additional.object.TaskStatus;
import net.hibernate.additional.repository.SessionRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class TaskServiceTest {
    TaskService taskService;
    TaskEntity taskEntity;
    static SessionObject sessionObject=null;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3")
            .withUsername("anton")
            .withPassword("anton")
            .withReuse(true)
            .withDatabaseName("postgres");
    public static void createTableConsistency()  {
        try(
                Connection connection= DriverManager.getConnection(
                        postgres.getJdbcUrl(),postgres.getUsername(),postgres.getPassword()))
        {
            String createTable="create table users ("
                    + "user_id      bigint, "
                    +"password     varchar(255),"
                    +"user_name    varchar(255),"
                    +"task_task_id bigint)";
            String insertTable="INSERT INTO users (task_task_id,user_name, password) VALUES "
                    +"(2,'ADMIN', 'ADMIN'),"
                    +"(3,'Sergej', 'Sergej');";
            PreparedStatement statCreate = connection.prepareStatement(createTable);
            Integer created = statCreate.executeUpdate();
            PreparedStatement statement = connection.prepareStatement(insertTable);
            Integer resultSet = statement.executeUpdate();

            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    @BeforeAll
    static void beforeAll() {
        sessionObject= SessionObject.builder()
                .name("ADMIN")
                .password("ADMIN")
                .build();
        postgres.start();
    }
    @AfterAll
    static void afterAll()  {
        postgres.stop();
    }
    @BeforeEach
    void setUp() throws LiquibaseException, SQLException {
        Connection connection= DriverManager.getConnection(
        postgres.getJdbcUrl(),postgres.getUsername(),postgres.getPassword());
        Database dataBase= DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase=new Liquibase("liquibase/dev/dbchangelog.xml",new ClassLoaderResourceAccessor(),dataBase);
        liquibase.update();
        dataBase.close();
        connection.close();
        taskService=new TaskService(new SessionRepo());
        try(Session session=new SessionRepo().getSession().openSession()){
            Transaction transaction=session.beginTransaction();
            UserEntity userEntity=UserEntity.builder()
                    .userName("ADMIN")
                    .password("ADMIN")
                    .build();
            CommentEntity commentEntity=CommentEntity.builder()
                    .comment("First Comment")
                    .user(userEntity)
                    .build();
                    taskEntity=TaskEntity.builder()
                    .name("task1")
                    .comments(List.of(commentEntity))
                    .user(userEntity)
                    .status(TaskStatus.IN_PROGRESS)
                    .createDate(new Date(1212121212121L))
                    .build();
            TagEntity tagEntity=TagEntity.builder()
                    .str("tag1")
                    .task(Set.of(taskEntity))
                    .build();
            taskEntity.setTag(Set.of(tagEntity));
            session.persist(commentEntity);
            session.persist(tagEntity);
            session.persist(userEntity);
            session.persist(taskEntity);
            transaction.commit();
        }
        try(Session session=new SessionRepo().getSession().openSession()) {
            Transaction transaction = session.beginTransaction();
            taskEntity.setCreateDate(new Date(1212121212121L));
            session.merge(taskEntity);
            transaction.commit();
            
            Query<TaskEntity> newTask=session.createQuery("from TaskEntity",TaskEntity.class);
            List<TaskEntity> newTaskEntity=newTask.list();
            for(TaskEntity ta:newTaskEntity){
                System.out.println("EDIT task ta="+ta);
            }
        }
    }
    @AfterEach
    void tearDown() throws SQLException, LiquibaseException {
        Connection connection= DriverManager.getConnection(
                postgres.getJdbcUrl(),postgres.getUsername(),postgres.getPassword());
        Database dataBase= DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase=new Liquibase("liquibase/dev/dbchangelog.xml",new ClassLoaderResourceAccessor(),dataBase);
        liquibase.rollback("initialState","legacy");
        dataBase.close();
        connection.close();
    }
    @Test
    void listAllTasks() throws AuthenticationException {
        SessionRepository sessRepo= new SessionRepo();
        List<TaskDTO> taskDtoList = taskService.listAllTasks(sessionObject, 0, 3);
        assertEquals("task1",taskDtoList.get(0).getName());
    }
    @Test
    void editTask() throws AuthenticationException, NoPermissionException {
        TaskCommandDTO taskCommandDTO=null;
        String taskChangedTitle="";
        try(Session session=new SessionRepo().getSession().openSession()) {
            taskEntity.setTitle("TITLE_1");
            TaskCommandDtoEntityMapper commandToEntityMapper=TaskCommandDtoEntityMapper.INSTANCE;
            taskCommandDTO=commandToEntityMapper.toDTO(taskEntity);
            session.refresh(taskEntity);
            taskService.editTask(taskCommandDTO,sessionObject);
            Query titleQuery=session.createNativeQuery("select task_title from tasks t where name=:task1");
            titleQuery.setParameter("task1","task1");
            taskChangedTitle =(String) titleQuery.list().get(0);
        }
        assertEquals("TITLE_1",taskChangedTitle);
    }
    @Test
    void createTask() throws AuthenticationException, NoPermissionException {
        TaskCommandDTO taskCommandDTO=TaskCommandDTO.builder()
                .name("task 2")
                .build();
        TaskDTO taskDTO=taskService.createTask(taskCommandDTO,sessionObject);
        assertEquals("task 2",taskDTO.getName());
    }
    @Test
    void deleteTask() throws AuthenticationException, NoPermissionException {
        TaskCommandDTO taskCommandDTO=null;
        int emptiNess=0;
        try(Session session=new SessionRepo().getSession().openSession()) {
            Query<TaskEntity> newTask = session.createQuery("from TaskEntity", TaskEntity.class);
            TaskEntity taskEntity = newTask.list().get(0);
            TaskCommandDtoEntityMapper commandToEntityMapper = TaskCommandDtoEntityMapper.INSTANCE;
            taskCommandDTO = commandToEntityMapper.toDTO(taskEntity);
            taskService.deleteTask(taskCommandDTO,sessionObject);
            Query<TaskEntity> empty = session.createQuery("from TaskEntity", TaskEntity.class);
            emptiNess = empty.list().size();
        }
        assertEquals(0,emptiNess);
    }
    @Test
    void getAllCount() {
        Long count=null;
        try(Session session=new SessionRepo().getSession().openSession()) {
            Query query = session.createQuery("select count(*) from TaskEntity ");
            count=(Long)query.uniqueResult();
        }
        assertEquals(1,count);
    }
    @Test
    void getIdOfTag() {
        String tagTitle="tag1";
        Long resultLong=0L;
        Long tagIndex=taskService.getIdOfTag(tagTitle);
        try(Session session=new SessionRepo().getSession().openSession()) {
            NativeQuery<Long> lon=session.createNativeQuery("select tag_id from tags where str=:tagStr",Long.class);
            lon.setParameter("tagStr",tagTitle);
            resultLong=lon.getSingleResultOrNull();
        }
        assertEquals(resultLong,tagIndex);
    }
    @Test
    void getAuthenticatedUser() throws AuthenticationException, NoPermissionException {
        UserDTO userDTO=taskService.getAuthenticatedUser(sessionObject);
        assertEquals("ADMIN",userDTO.getUserName());
    }
    private static class SessionRepo implements SessionRepository{
        private static SessionFactory sessionFactory;
        static {
            Properties properties = new Properties();
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
            properties.setProperty("hibernate.connection.username", postgres.getUsername());
            properties.setProperty("hibernate.connection.password", postgres.getPassword());
            properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            properties.setProperty("hibernate.show_sql", "true");
            sessionFactory=new Configuration()
                    .setProperties(properties)
                    .addAnnotatedClass(TaskEntity.class)
                    .addAnnotatedClass(TagEntity.class)
                    .addAnnotatedClass(UserEntity.class)
                    .addAnnotatedClass(CommentEntity.class)
                    .buildSessionFactory();
        }
        public  SessionFactory getSession(){
            return sessionFactory;
        }
    }
}