package net.hibernate.additional.controller;

//userName=ADMIN&password=ADMIN
import net.hibernate.additional.dto.UserDTO;
import net.hibernate.additional.exception.AuthenticationException;
import net.hibernate.additional.object.SessionObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

import net.hibernate.additional.repository.SessionRepoHelper;
import net.hibernate.additional.service.UserRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "registerServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("logger",logger);
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession currentSession = request.getSession();
        ServletContext servletContext = getServletContext();
        Logger logger=(Logger) servletContext.getAttribute("logger");
        SessionObject sessionObject=(SessionObject) currentSession.getAttribute("session");
        String userName=request.getParameter("userName");
        String password=request.getParameter("password");
        logger.info("The "+userName+" User is registered");
        if(userName==null||password==null||userName.isEmpty()) {
            if(sessionObject==null) {
                request.setAttribute("pleaseEnterUserNamePassword", "Please Enter UserName and Password");
                request.getRequestDispatcher("/register.jsp").include(request, response);
                return;
            }
        }else{
            if (sessionObject == null) {
                sessionObject = SessionObject.builder()
                        .sessionId(currentSession.getId())
                        .name(userName)
                        .password(password)
                        .build();
                currentSession.setAttribute("session", sessionObject);
            }else{
                sessionObject.setName(userName);
                sessionObject.setPassword(password);
                logger.info("registerServlet set username and password");
            }
        }
        UserRegistrationService userRegistrationService=new UserRegistrationService(new SessionRepoHelper());
        UserDTO isAuth= null;
        try {
            isAuth = userRegistrationService.getUserDTO(userName,password);
        } catch (AuthenticationException e) {
            request.getRequestDispatcher("/register.jsp").include(request, response);
            return ;
        }
        String isAuthString="NoName";
        if (isAuth==null){
            userRegistrationService.registerUser(userName,password);
        }else{
            isAuthString=isAuth.getUserName();
        }
        request.setAttribute("ObjectUserName",""+isAuthString);
        request.getRequestDispatcher("/register.jsp").include(request, response);
    }
}