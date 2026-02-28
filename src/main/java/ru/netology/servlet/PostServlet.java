package ru.netology.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.netology.config.AppConfig;
import ru.netology.domain.Post;
import ru.netology.repository.InMemoryPostRepo;
import ru.netology.repository.PostRepository;

import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@jakarta.servlet.annotation.WebServlet(urlPatterns = "/posts")
public class PostServlet extends HttpServlet {
    private final PostRepository repository = new InMemoryPostRepo();
    private final Gson gson = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType(AppConfig.CONTENT_TYPE);

        String pathInfo = req.getPathInfo();
        String method = req.getMethod();

        try {
            if (pathInfo == null) {
                if ("GET".equals(method)) {
                    getAll(resp);
                } else if ("POST".equals(method)) {
                    save(req, resp);
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
                    getById(resp, id);
                } else if ("DELETE".equals(method)) {
                    removeById(resp, id);
                } else {
                    resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    private void getAll(HttpServletResponse resp) throws IOException {
        var data = repository.all();
        writeResponse(resp, gson.toJson(data));
    }

    private void getById(HttpServletResponse resp, int id) throws IOException {
        var post = repository.getById(id);
        if (post == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        writeResponse(resp, gson.toJson(post));
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readRequest(req);
        Post post = gson.fromJson(body, Post.class);

        Post saved = repository.save(post);

        if (saved == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        writeResponse(resp, gson.toJson(saved));
    }

    private void removeById(HttpServletResponse resp, int id) {
        repository.removeById(id);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private String readRequest(HttpServletRequest req) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private void writeResponse(HttpServletResponse resp, String data) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.print(data);
        }
    }
}
