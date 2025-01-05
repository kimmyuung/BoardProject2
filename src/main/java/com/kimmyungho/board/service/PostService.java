package com.kimmyungho.board.service;

import com.kimmyungho.board.model.Post;
import com.kimmyungho.board.model.PostPathRequestBody;
import com.kimmyungho.board.model.PostPostRequestBody;
import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.repository.PostEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class
PostService {

    @Autowired private PostEntityRepository postEntityRepository;

    public List<Post> getPosts() {
        List<PostEntity> posteitities = postEntityRepository.findAll();
        return posteitities.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var postEntity =
                postEntityRepository
                .findById(postId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GET Post ID not found.")
                );
        /* posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();*/
        return Post.from(postEntity);
    }

    public Post createPost(PostPostRequestBody postPostRequestBody) {
        var postEntity = new PostEntity();
            postEntity.setBody(postPostRequestBody.getBody());
        var savedPostEntity = postEntityRepository.save(postEntity);
        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPathRequestBody postPathRequestBody) {
        var postEntity =
                postEntityRepository
                        .findById(postId)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UPDATE Post not found.")
                        );

        postEntity.setBody(postPathRequestBody.getBody());
        var updatePostEntity = postEntityRepository.save(postEntity);

        return Post.from(updatePostEntity);
    }

    public void deletePost(Long postId) {
        var postEntity =
                postEntityRepository
                        .findById(postId)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DELETE Post not found.")
                        );
        postEntityRepository.delete(postEntity);
    }
}
