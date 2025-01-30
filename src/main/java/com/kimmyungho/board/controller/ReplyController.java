package com.kimmyungho.board.controller;

import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.reply.Reply;
import com.kimmyungho.board.model.reply.ReplyPathRequestBody;
import com.kimmyungho.board.model.reply.ReplyPostRequestBody;
import com.kimmyungho.board.service.ReplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/replies")
public class ReplyController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyController.class);

    @Autowired private ReplyService replyService;


    @GetMapping
    public ResponseEntity<List<Reply>> getRepliesByPostId(@PathVariable Long postId) {
        logger.info("GET /api/posts/{postId}/replies");
        var replies = replyService.getRepliesByPostId(postId);
        return ResponseEntity.ok(replies);
    }

    @PostMapping
    public ResponseEntity<Reply> createReply(
            @PathVariable Long postId,
            @RequestBody ReplyPostRequestBody replyPostRequestBody,
            Authentication authentication) {

        var reply = replyService.createPost
                (postId,  replyPostRequestBody , (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable Long postId,
                                             @PathVariable Long replyId,
                                             @RequestBody ReplyPathRequestBody replyPathRequestBody, Authentication authentication) {
        var updateReply = replyService.updateReply(postId, replyId ,replyPathRequestBody, (UserEntity) authentication.getPrincipal());
        return ResponseEntity.ok(updateReply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long postId,
            @PathVariable Long replyId , Authentication authentication) {
        replyService.deleteReply(postId, replyId , (UserEntity) authentication.getPrincipal());
        return ResponseEntity.noContent().build();
    }

}
