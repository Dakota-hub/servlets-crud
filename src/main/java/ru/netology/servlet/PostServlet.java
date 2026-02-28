package ru.netology.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AppConfig;
import ru.netology.controller.PostController;

import java.io.IOException;

@jakarta.servlet.annotation.WebServlet(urlPatterns = "/posts")
public class PostServlet extends HttpServlet {
    private PostController controller;

    @Override
    public void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        String method = req.getMethod();

        try {
            if (pathInfo == null) {
                if ("GET".equals(method)) {
                    controller.all(resp);
                } else if ("POST".equals(method)) {
                    controller.save(req.getReader(), resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                }
            } else {
                String idPart = pathInfo.substring(1);
                if (!idPart.matches("\\d+")) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                int id = Integer.parseInt(idPart);

                if ("GET".equals(method)) {
                    controller.getById(id, resp);
                } else if ("DELETE".equals(method)) {
                    controller.removeById(id, resp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}