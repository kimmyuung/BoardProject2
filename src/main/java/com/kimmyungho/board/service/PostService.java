package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.post.PostNotFoundException;
import com.kimmyungho.board.model.post.Post;
import com.kimmyungho.board.model.post.PostPathRequestBody;
import com.kimmyungho.board.model.post.PostPostRequestBody;
import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.repository.PostEntityRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class
PostService {

   // @Autowired private PostEntityRepository postEntityRepository;
    private final PostEntityRepository postEntityRepository;

    public PostService(PostEntityRepository postEntityRepository) {
        this.postEntityRepository = postEntityRepository;
    }


    public List<Post> getPosts() {
        List<PostEntity> posteitities = postEntityRepository.findAll();
        return posteitities.stream().map(Post::from).toList();
    }

    public Post getPostByPostId(Long postId) {
        var postEntity =
                postEntityRepository
                .findById(postId)
                .orElseThrow(
                        () -> new PostNotFoundException(postId)
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
                                () -> new PostNotFoundException(postId)
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
                                () -> new PostNotFoundException(postId)
                        );
        postEntityRepository.delete(postEntity);
    }
}
