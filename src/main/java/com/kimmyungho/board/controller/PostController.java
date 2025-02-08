package com.kimmyungho.board.controller;

import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.post.Post;
import com.kimmyungho.board.model.post.PostPathRequestBody;
import com.kimmyungho.board.model.post.PostPostRequestBody;
import com.kimmyungho.board.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired private PostService postService;


    @GetMapping
    public ResponseEntity<List<Post>> getPosts(Authentication authentication) {
        logger.info("GET /api/v1/posts");
        var posts = postService.getPosts((UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId
    , Authentication authentication) {
        logger.info("GET /api/v1/posts/{}", postId);
        var post = postService.getPostByPostId(postId, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody, Authentication authentication) {
        logger.info("POST /api/v1/posts");
        var createPost = postService.createPost(postPostRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(createPost);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId, @RequestBody PostPathRequestBody postPathRequestBody, Authentication authentication) {
        logger.info("PATCH /api/v1/posts/{}" , postId);
        var updatePost = postService.updatePost(postId , postPathRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(updatePost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId , Authentication authentication) {
        logger.info("DELETE /api/v1/posts/{}" , postId);
        postService.deletePost(postId , (UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Post> toggleLike(
            @PathVariable Long postId , Authentication authentication) {
        var post = postService.togglelike(postId , (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(post);
    }

}
