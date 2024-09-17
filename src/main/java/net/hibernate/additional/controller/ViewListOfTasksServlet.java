package net.hibernate.additional.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.annotation.
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.hibernate.additional.command.TagCommandDTO;
import net.hibernate.additional.command.TaskCommandDTO;
import net.hibernate.additional.dto.TaskDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.exception.NoPermissionException;
import net.hibernate.additional.object.SessionObject;
import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "viewRequestServlet", value = "/task")
public class ViewListOfTasksServlet extends HttpServlet {
    public void init() {
        Logger logger = LoggerFactory.getLogger(ViewListOfTasksServlet.class);
        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("logger",logger);
        TaskService taskService=new TaskService(new SessionRepoHelper());
        servletContext.setAttribute("service",taskService);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        HttpSession currentSession = request.getSession();
        String pageNumber = request.getParameter("pageNumber");
        String pageSize = request.getParameter("pageSize");
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        TaskService taskService=(TaskService) servletContext.getAttribute("service");
        Logger logger=(Logger)servletContext.getAttribute("logger");
        String workerName="";
        if(sessionObject==null){
            sessionObject=SessionObject.builder()
                    .sessionId(currentSession.getId())
                    .build();
            currentSession.setAttribute("session",sessionObject);
        }else{
            workerName=sessionObject.getName();

        }
        List<TaskDTO> taskDTOList=null;
        try {
            taskDTOList= taskService.listAllTasks(sessionObject, Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
        }catch(AuthenticationException e){
            response.sendError(404, "User name= "+e.getMessage()+"; User name= "+workerName+" have wrong password or not registered");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String fromDtoToJson="";
        try {
            fromDtoToJson=objectMapper.writeValueAsString(taskDTOList);
        }catch(JsonProcessingException e){
            logger.error("JSON processing exception");
        }
        response.setStatus(200);
        response.setContentType("application/json");//"text/html;charset=UTF-8");
        PrintWriter out =  response.getWriter();
        out.println(fromDtoToJson);
        out.close();
    }
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        ServletContext servletContext = getServletContext();
        HttpSession currentSession = request.getSession();
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        List<TaskCommandDTO> taskCommandDto=jacksonProcessing(request);

        TaskService taskService=(TaskService) servletContext.getAttribute("service");
        boolean boolSuccess=false;
        for (TaskCommandDTO taskCommand:taskCommandDto) {
            try {
                boolSuccess = taskService.editTask(taskCommand,sessionObject);//(taskCommandDto)
            } catch (AuthenticationException e) {
                response.sendError(404, e.getMessage()+sessionObject.getName()+" have wrong password or not registered");
            }catch(NoPermissionException e){
                response.sendError(404, e.getMessage()+sessionObject.getName()+" dont have permission");
            }
        }
        if (boolSuccess==true){
            response.setStatus(200);
        }else response.setStatus(404);
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        ServletContext servletContext = getServletContext();
        HttpSession currentSession = request.getSession();
        Logger logger=(Logger)servletContext.getAttribute("logger");
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        TaskService taskService=(TaskService) servletContext.getAttribute("service");
        List<TaskCommandDTO> taskCommandDto=jacksonProcessing(request);
        TaskDTO taskDTO=null;
        for(TaskCommandDTO taskCommand:taskCommandDto) {
            try {
                taskDTO = taskService.createTask(taskCommand,sessionObject);//taskCommandDto
            } catch (AuthenticationException e) {
                response.sendError(404, "User name "+sessionObject.getName()+" have wrong password or not registered");
            }catch(NoPermissionException e){
                response.sendError(404, "User name "+sessionObject.getName()+" but need ADMIN permission");
            }

        }
        ObjectMapper objectMapper = new ObjectMapper();
        String fromDtoToJson="";
        try {
            fromDtoToJson=objectMapper.writeValueAsString(taskDTO);
        }catch(JsonProcessingException e){
            logger.error("JSON processing exception");
        }
        response.setContentType("application/json");
        PrintWriter out =  response.getWriter();
        out.println(fromDtoToJson);
        out.close();
    }
    private List<TaskCommandDTO> jacksonProcessing(HttpServletRequest request)throws IOException{
        ServletContext servletContext = getServletContext();
        HttpSession currentSession = request.getSession();
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        TaskService taskService=(TaskService) servletContext.getAttribute("service");
        BufferedReader buffer=request.getReader();

        String json=buffer.lines().collect(Collectors.joining());
        ObjectMapper objectMapper=new ObjectMapper();
        TaskCommandDTO taskCommandDto=null;
        try {
            taskCommandDto = objectMapper.readValue(json, TaskCommandDTO.class);
        }catch(JsonMappingException e){
            throw new IOException("Cant map JSon file",e);
        }catch(JsonProcessingException e){
            throw new IOException("Cant process JSon file",e);
        }
        Set<TagCommandDTO> tagCommandDto=taskCommandDto.getTag();
        String tagString="";
        for (TagCommandDTO tag:tagCommandDto){
            tagString=tag.getStr()+" ";
        }
        Set<TagCommandDTO> tagCommandSet=new HashSet<>();
        tagString=tagString.trim();
        List<TaskCommandDTO> taskSet=new ArrayList<>();
        for(String tag:tagString.split("\\p{Zs}")){
            tag=tag.trim();
            if(tag.isEmpty())continue;
            Long tagId=taskService.getIdOfTag(tag);
            TagCommandDTO tagCommandDTO=new TagCommandDTO();
            tagCommandDTO.setStr(tag);
            tagCommandDTO.setTag_id(tagId);
            tagCommandSet.add(tagCommandDTO);
        }
        taskCommandDto.setTag(tagCommandSet);
        taskSet.add(taskCommandDto);
        return taskSet;
    }
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        ServletContext servletContext = getServletContext();
        Logger logger=(Logger)servletContext.getAttribute("logger");
        HttpSession currentSession = request.getSession();
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        TaskService taskService=(TaskService) servletContext.getAttribute("service");
        List<TaskCommandDTO> taskCommandDto=jacksonProcessing(request);
        logger.info("deletion DTO="+taskCommandDto);
        boolean isSuccessFull=false;
        for(TaskCommandDTO taskCommand:taskCommandDto) {
            try{
                isSuccessFull = taskService.deleteTask(taskCommand,sessionObject);//(taskCommandDto)
            } catch (AuthenticationException e) {
                response.sendError(404, "User name= "+e.getMessage()+" name="+sessionObject.getName()+" have wrong password or not registered");
            }catch(NoPermissionException e){
                response.sendError(404, "User name= "+e.getMessage()+" name="+sessionObject.getName()+" but need ADMIN permission");
            }
        }
        if (isSuccessFull)response.setStatus(200);
        else response.setStatus(404);
    }

}
