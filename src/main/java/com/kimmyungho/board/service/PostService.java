package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.post.PostNotFoundException;
import com.kimmyungho.board.exception.user.UserNotAllowedException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.LikeEntity;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.post.Post;
import com.kimmyungho.board.model.post.PostPathRequestBody;
import com.kimmyungho.board.model.post.PostPostRequestBody;
import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.repository.LikeEntityRepository;
import com.kimmyungho.board.repository.PostEntityRepository;
import com.kimmyungho.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class
PostService {

    @Autowired private PostEntityRepository postEntityRepository;
    @Autowired private UserEntityRepository userEntityRepository;

    @Autowired private LikeEntityRepository likeEntityRepository;

    public List<Post> getPosts(UserEntity currentUser) {
        List<PostEntity> postEntities = postEntityRepository.findAll();
        return postEntities.stream()
                .map(postEntity -> getPostWithLikgingStatus(postEntity, currentUser))
                .toList();
    }

    public Post getPostByPostId(Long postId, UserEntity currentUser) {
        var postEntity = postEntityRepository
                .findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        /* posts.stream().filter(post
                -> postId.equals(post.getPostId())).findFirst();*/
        return getPostWithLikgingStatus(postEntity, currentUser);
    }

    private Post getPostWithLikgingStatus(PostEntity postEntity, UserEntity currentUser) {
        var isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity).isPresent();
        return Post.from(postEntity, isLiking);
    }

    public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentuser) {

        var savedPostEntity = postEntityRepository.save(
                PostEntity.of(postPostRequestBody.getBody(), currentuser)
        );
        return Post.from(savedPostEntity);
    }

    public Post updatePost(Long postId, PostPathRequestBody postPathRequestBody, UserEntity currentuser) {
        var postEntity =
                postEntityRepository
                        .findById(postId)
                        .orElseThrow(
                                () -> new PostNotFoundException(postId)
                        );

        if (! postEntity.getUser().equals(currentuser)) {
            throw new UserNotAllowedException();
        }
        postEntity.setBody(postPathRequestBody.getBody());
        var updatePostEntity = postEntityRepository.save(postEntity);

        return Post.from(updatePostEntity);
    }

    public void deletePost(Long postId, UserEntity currentuser) {
        var postEntity =
                postEntityRepository
                        .findById(postId)
                        .orElseThrow(
                                () -> new PostNotFoundException(postId)
                        );
        if (! postEntity.getUser().equals(currentuser)) {
            throw new UserNotAllowedException();
        }
        postEntityRepository.delete(postEntity);
    }

    public List<Post> getPostsByUsername(String username ,UserEntity currentUser) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        var postEntities =postEntityRepository.findByUser(userEntity);
        return postEntities.stream()
                .map(postEntity -> getPostWithLikgingStatus(postEntity, currentUser))
                .toList();
    }

    @Transactional
    public Post togglelike(Long postId, UserEntity currentUser) {
        var postEntity =
                postEntityRepository.findById(postId).orElseThrow( () -> new PostNotFoundException(postId));
        var likeEntity = likeEntityRepository.findByUserAndPost(currentUser, postEntity);
        if (likeEntity.isPresent()) {
            likeEntityRepository.delete(likeEntity.get());
            postEntity.setLikesCount(Math.max(0, postEntity.getLikesCount() - 1));
            return Post.from(postEntityRepository.save(postEntity), false);
        } else {
            likeEntityRepository.save(LikeEntity.of(currentUser, postEntity));
            postEntity.setLikesCount(postEntity.getLikesCount() + 1);
            return Post.from(postEntityRepository.save(postEntity), true);
        }

    }
}
