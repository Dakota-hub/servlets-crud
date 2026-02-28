package ru.netology.repository;

import ru.netology.domain.Post;

import java.util.List;

public interface PostRepository {
    List<Post> all();

    Post save(Post post);

    Post getById(int id);

    void removeById(int id);
}
