package com.kimmyungho.board.service;

import com.kimmyungho.board.model.Post;
import com.kimmyungho.board.model.PostPathRequestBody;
import com.kimmyungho.board.model.PostPostRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class
PostService {

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

    public Optional<Post> getPostByPostId(Long postId) {
        return posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();
    }

    public Post createPost(PostPostRequestBody postPostRequestBody) {
        Long newPostId
                = posts.stream().mapToLong(Post::getPostId).max().orElse(0L) + 1 ;
        Post newPost = new Post(newPostId, postPostRequestBody.getBody(), ZonedDateTime.now());
        posts.add(newPost);

        return newPost;
    }

    public Post updatePost(Long postId, PostPathRequestBody postPathRequestBody) {
        Optional<Post> postOptional = posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();

        if (postOptional.isPresent()) {
            Post postToUpdate = postOptional.get();
            postToUpdate.setBody(postPathRequestBody.getBody());
            return postToUpdate;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not Found");
        }
    }

    public void deletePost(Long postId) {
        Optional<Post> postOptional = posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();
        if (postOptional.isPresent()) {
            Post postToDelete = postOptional.get();
            posts.remove(postToDelete);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not Found");
        }
    }
}
