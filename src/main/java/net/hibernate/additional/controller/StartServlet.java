package net.hibernate.additional.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.hibernate.additional.object.SessionObject;
import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "startServlet", value = "/start")
public class StartServlet extends HttpServlet {
    public void init() {
        Logger logger = LoggerFactory.getLogger(ViewListOfTasksServlet.class);
        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("logger", logger);
        TaskService taskService = new TaskService(new SessionRepoHelper());
        servletContext.setAttribute("service", taskService);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        HttpSession currentSession = request.getSession();
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        request.setAttribute("ObjectUserName",sessionObject.getName());
        String pageNumber = request.getParameter("pageNumber");
        String pageSize = request.getParameter("pageSize");
        if(sessionObject != null&& ! sessionObject.getName().equals("ADMIN")) {
            String hideForm = " hidden='true' ";
            request.setAttribute("hideForm", hideForm);
        }
        request.getRequestDispatcher("/html/editingTask.jsp").include(request, response);
    }
}