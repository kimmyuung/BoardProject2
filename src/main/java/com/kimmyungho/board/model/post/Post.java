package com.kimmyungho.board.model.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.model.user.User;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
 public record Post(
         Long postId,
         String body,
         User user,
         Long repliesCount,
         Long likesCount,
         ZonedDateTime createDateTime,
         ZonedDateTime updateDateTime,
         ZonedDateTime deleteDateTime,
         Boolean isLiking
 ) {
    public static Post from(PostEntity postEntity) {
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                User.from(postEntity.getUser()),
                postEntity.getRepliesCount(),
                postEntity.getLikesCount(),
                postEntity.getCreatedDateTime(),
                postEntity.getUpdatedDateTime(),
                postEntity.getDeletedDateTime(),
                null
                 );
    }

    public static Post from(PostEntity postEntity, boolean isLiking) {
        return new Post(
                postEntity.getPostId(),
                postEntity.getBody(),
                User.from(postEntity.getUser()),
                postEntity.getRepliesCount(),
                postEntity.getLikesCount(),
                postEntity.getCreatedDateTime(),
                postEntity.getUpdatedDateTime(),
                postEntity.getDeletedDateTime(),
                isLiking
        );
    }
}
