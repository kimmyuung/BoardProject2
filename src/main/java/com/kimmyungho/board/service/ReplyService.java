package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.post.PostNotFoundException;
import com.kimmyungho.board.exception.reply.ReplyNotFoundException;
import com.kimmyungho.board.exception.user.UserNotAllowedException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.ReplyEntity;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.reply.Reply;
import com.kimmyungho.board.model.reply.ReplyPathRequestBody;
import com.kimmyungho.board.model.reply.ReplyPostRequestBody;
import com.kimmyungho.board.repository.PostEntityRepository;
import com.kimmyungho.board.repository.ReplyEntityRepository;
import com.kimmyungho.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class
ReplyService {

    @Autowired private ReplyEntityRepository replyEntityRepository;
    @Autowired private PostEntityRepository postEntityRepository;
    @Autowired
    private UserEntityRepository userEntityRepository;

    public List<Reply> getRepliesByPostId(Long postId) {
        var postEntity =
                postEntityRepository
                        .findById(postId)
                        .orElseThrow(
                                () -> new PostNotFoundException(postId)
                        );
        var replyEntities = replyEntityRepository.findByPost(postEntity);
        return replyEntities.stream().map(Reply::from).toList();
    }


    @Transactional // DB 상에서 실행시 묶어서 실행
    public Reply createPost(Long postId ,ReplyPostRequestBody replyPostRequestBody, UserEntity currentuser) {
        var postEntity =
                            postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        var createdReply =
        replyEntityRepository.save(
                ReplyEntity.of(replyPostRequestBody.getBody(), currentuser, postEntity));

        postEntity.setRepliesCount(postEntity.getRepliesCount() + 1);

        return Reply.from(createdReply);
    }

    public Reply updateReply(Long postId, Long replyId, ReplyPathRequestBody replyPathRequestBody, UserEntity currentuser) {
//        var postEntity =
//                postEntityRepository
//                        .findById(postId)
//                        .orElseThrow(
//                                () -> new PostNotFoundException(postId)
//                        );
//        if (! postEntity.getUser().equals(currentuser)) {
//            throw new UserNotAllowedException();
//        }
        var replyEntity =
                replyEntityRepository.findById(replyId)
                        .orElseThrow( () -> new ReplyNotFoundException(replyId));

        if (!replyEntity.getUser().equals(currentuser)) { // 댓글 작성자와 현재 로그인 유저가 같은지 확인
         throw new UserNotAllowedException();
        }
        replyEntity.setBody(replyPathRequestBody.getBody());
        return Reply.from(replyEntityRepository.save(replyEntity));
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, UserEntity currentuser) {
        var postEntity = postEntityRepository.findById(postId).orElseThrow( () -> new PostNotFoundException(postId));
        var replyEntity =
                replyEntityRepository.findById(replyId)
                        .orElseThrow( () -> new ReplyNotFoundException(replyId));
        if (!replyEntity.getUser().equals(currentuser)) { // 댓글 작성자와 현재 로그인 유저가 같은지 확인
            throw new UserNotAllowedException();
        }
        replyEntityRepository.delete(replyEntity);

        postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));
        postEntityRepository.save(postEntity);
    }

    public List<Reply> getRepliesByUser(String username) {
        var userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow( () -> new UserNotFoundException(username));

        var replies = replyEntityRepository.findByUser(userEntity);
        return replies.stream().map(Reply::from).toList();
    }
}
