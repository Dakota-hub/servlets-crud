package ru.netology.service;

import ru.netology.domain.Post;
import ru.netology.repository.PostRepository;

import java.util.List;

public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> findAll() {
        return repository.all();
    }

    public Post findById(int id) {
        return repository.getById(id);
    }

    public Post save(Post post) {
        return repository.save(post);
    }

    public void deleteById(int id) {
        repository.removeById(id);
    }
}