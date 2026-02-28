package ru.netology.repository;

import ru.netology.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryPostRepo implements PostRepository {
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public List<Post> all() {
        return new ArrayList<>(posts.values());
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            int newId = counter.incrementAndGet();
            post.setId(newId);
            posts.put(newId, post);
        } else {
            if (!posts.containsKey(post.getId())) {
                return null;
            }
            posts.put(post.getId(), post);
        }
        return post;
    }

    @Override
    public Post getById(int id) {
        return posts.get(id);
    }

    @Override
    public void removeById(int id) {
        posts.remove(id);
    }
}
