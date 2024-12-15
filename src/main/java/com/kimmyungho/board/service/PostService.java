package com.kimmyungho.board.service;

import com.kimmyungho.board.model.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private static final List<Post> posts = new ArrayList<>();

    static {
        posts.add(new Post(1L, "Post 1" , ZonedDateTime.now()));
        posts.add(new Post(2L, "Post 2" , ZonedDateTime.now()));
        posts.add(new Post(3L, "Post 3" , ZonedDateTime.now()));
        posts.add(new Post(4L, "Post 4" , ZonedDateTime.now()));
    }

    public List<Post> getPosts() {
        return posts;
    }

    public Optional<Post> geePostByPostId(Long postId) {
        return posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();
    }

}
