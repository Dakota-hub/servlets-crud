package ru.netology.controller;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.netology.domain.Post;
import ru.netology.service.PostService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@Controller
public class PostController {
    private final PostService service;
    private final Gson gson;

    @Autowired
    public PostController(PostService service) {
        this.service = service;
        this.gson = new Gson();
    }

    public void all(HttpServletResponse resp) throws IOException {
        var data = service.findAll();
        writeResponse(resp, gson.toJson(data));
    }

    public void getById(int id, HttpServletResponse resp) throws IOException {
        var post = service.findById(id);
        if (post == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        writeResponse(resp, gson.toJson(post));
    }

    public void save(BufferedReader reader, HttpServletResponse resp) throws IOException {
        String body = reader.lines().collect(Collectors.joining("\n"));
        Post post = gson.fromJson(body, Post.class);

        Post saved = service.save(post);

        if (saved == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        writeResponse(resp, gson.toJson(saved));
    }

    public void removeById(int id, HttpServletResponse resp) {
        service.deleteById(id);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void writeResponse(HttpServletResponse resp, String data) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.print(data);
        }
    }
}
