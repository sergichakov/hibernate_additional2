package net.hibernate.additional.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.hibernate.additional.command.CommentCommandDTO;
import net.hibernate.additional.command.TaskCommandDTO;
import net.hibernate.additional.dto.CommentDTO;
import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.object.SessionObject;
import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.service.CommentService;
import net.hibernate.additional.service.TaskService;
import net.hibernate.additional.service.UserRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "CommentServlet", value = "/comment")
public class CommentServlet extends HttpServlet {
    private volatile Logger logger = null;
    private volatile CommentService commentService;
    private volatile UserRegistrationService registerService;
    public void init() {
        logger = LoggerFactory.getLogger(CommentServlet.class);
        commentService = new CommentService(new SessionRepoHelper());
        registerService = new UserRegistrationService(new SessionRepoHelper());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CommentCommandDTO commentCommandDto=getCommentCommandDtoFromRequest(request);
        List<CommentDTO> commentDTOList = commentService.listAllComments(commentCommandDto.getTask(), null);
        String fromDtoToJson = "";
        try {
            fromDtoToJson = objectMapper.writeValueAsString(commentDTOList);
        } catch (JsonProcessingException e) {
            logger.error("JSON processing exception");
        }
        response.setStatus(200);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(fromDtoToJson);
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommentCommandDTO commentCommandDto=getCommentCommandDtoFromRequest(request);
        commentService.createComment(commentCommandDto);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommentCommandDTO commentCommandDto=getCommentCommandDtoFromRequest(request);
        commentService.editComment(commentCommandDto);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommentCommandDTO commentCommandDto=getCommentCommandDtoFromRequest(request);
        commentService.deleteComment(commentCommandDto);
    }

    private CommentCommandDTO getCommentCommandDtoFromRequest(HttpServletRequest request) throws IOException {
        BufferedReader buffer = request.getReader();
        String json = buffer.lines().collect(Collectors.joining());
        ObjectMapper objectMapper = new ObjectMapper();
        CommentCommandDTO commentCommandDto = null;
        try {
            commentCommandDto = objectMapper.readValue(json, CommentCommandDTO.class);
        } catch (JsonMappingException e) {
            throw new IOException("Cant map JSon file", e);
        } catch (JsonProcessingException e) {
            throw new IOException("Cant process JSon file", e);
        }
        return commentCommandDto;
    }
}
