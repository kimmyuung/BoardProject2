package com.kimmyungho.board.controller;

import com.kimmyungho.board.model.Post;
import com.kimmyungho.board.model.PostPathRequestBody;
import com.kimmyungho.board.model.PostPostRequestBody;
import com.kimmyungho.board.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        logger.info("GET /api/v1/posts");
        var posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        logger.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) {
        logger.info("POST /api/v1/posts");
        var createPost = postService.createPost(postPostRequestBody);
        return ResponseEntity.ok(createPost);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId, @RequestBody PostPathRequestBody postPathRequestBody) {
        logger.info("PATCH /api/v1/posts/{}" , postId);
        var updatePost = postService.updatePost(postId , postPathRequestBody);
        return ResponseEntity.ok(updatePost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId ) {
        logger.info("DELETE /api/v1/posts/{}" , postId);
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}
