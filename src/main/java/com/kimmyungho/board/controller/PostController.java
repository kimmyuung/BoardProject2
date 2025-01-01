package com.kimmyungho.board.controller;

import com.kimmyungho.board.model.Post;
import com.kimmyungho.board.model.PostPathRequestBody;
import com.kimmyungho.board.model.PostPostRequestBody;
import com.kimmyungho.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        var posts = postService.getPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
        Optional<Post> matchingPost = postService.getPostByPostId(postId);
        return matchingPost
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) {
        var createPost = postService.createPost(postPostRequestBody);
        return ResponseEntity.ok(createPost);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId, @RequestBody PostPathRequestBody postPathRequestBody) {
        var updatePost = postService.updatePost(postId , postPathRequestBody);
        return ResponseEntity.ok(updatePost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId ) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}
