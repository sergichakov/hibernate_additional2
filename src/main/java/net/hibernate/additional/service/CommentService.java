package net.hibernate.additional.service;

import net.hibernate.additional.command.CommentCommandDTO;

import net.hibernate.additional.command.TaskCommandDTO;
import net.hibernate.additional.command.UserCommandDTO;
import net.hibernate.additional.command.mapper.CommentCommandDtoEntityMapper;
import net.hibernate.additional.command.mapper.TaskCommandDtoEntityMapper;
import net.hibernate.additional.dto.CommentDTO;
import net.hibernate.additional.mapper.CommentEntityDtoMapper;
import net.hibernate.additional.model.CommentEntity;
import net.hibernate.additional.model.TaskEntity;
import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.repository.SessionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Order;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommentService {
    private Logger logger= null;
    private SessionRepository sessionRepoHelper;
    public CommentService(){
        logger= LoggerFactory.getLogger(TaskService.class);
    }
    public CommentService(SessionRepository sessionRepoHelper){
        this.sessionRepoHelper=sessionRepoHelper;
        logger= LoggerFactory.getLogger(TaskService.class);
    }
    public List<CommentDTO>listAllComments(TaskCommandDTO taskCommandDTO, String userName){
        TaskCommandDtoEntityMapper commandToEntityMapper=TaskCommandDtoEntityMapper.INSTANCE;
        TaskEntity taskEntity=commandToEntityMapper.toModel(taskCommandDTO);
        List<CommentEntity> commentList=null;
        try (Session session = sessionRepoHelper.getSession().openSession()) {
            Query<CommentEntity> comments;
            if (userName == null || userName.equals("ADMIN") || userName.isEmpty() || userName.equals("Unknown")) {
                comments = session.createQuery("from CommentEntity where task =:taskId", CommentEntity.class);
                comments.setParameter("taskId", taskEntity);
            } else {
                comments = session.createQuery("from CommentEntity where task =:taskId and name= :userName", CommentEntity.class);
                comments.setParameter("taskId", taskEntity);
                comments.setParameter("userName", userName);
            }
            comments.setOrder(Order.asc(CommentEntity.class, "user"));
            commentList=comments.list();
        }
        List<CommentDTO>commentDtoList=new ArrayList<>();
        for(CommentEntity commentEntity:commentList){
            CommentDTO commentDto= CommentEntityDtoMapper.INSTANCE.toDTO(commentEntity);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }
    public boolean editComment(CommentCommandDTO commentCommandDto){
        CommentCommandDtoEntityMapper commentCommandDtoEntityMapper=CommentCommandDtoEntityMapper.INSTANCE;
        CommentEntity commentEntity=commentCommandDtoEntityMapper.toModel(commentCommandDto);
        try(Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction=session.beginTransaction();
            session.merge(commentEntity);
            transaction.commit();
        }catch(ConstraintViolationException e){
            logger.info("attempt to insert new duplicate key into the table");
            return false;
        }
        return true;
    }
    public void createComment(CommentCommandDTO commentCommandDTO){
        CommentCommandDtoEntityMapper commentCommandToEntityMapper=CommentCommandDtoEntityMapper.INSTANCE;
        CommentEntity commentEntity=commentCommandToEntityMapper.toModel(commentCommandDTO);
        try(Session session = sessionRepoHelper.getSession().openSession()){
            Transaction transaction=session.beginTransaction();
            session.persist(commentEntity);
            transaction.commit();
        }
    }
    public boolean deleteComment(CommentCommandDTO commandDTO){
        CommentCommandDtoEntityMapper commandToEntityMapper=CommentCommandDtoEntityMapper.INSTANCE;
        CommentEntity commentEntity=commandToEntityMapper.toModel(commandDTO);
        try(Session session = sessionRepoHelper.getSession().openSession()) {
            Transaction transaction=session.beginTransaction();
            session.remove(commentEntity);
            transaction.commit();
        }
        return true;
    }

}
