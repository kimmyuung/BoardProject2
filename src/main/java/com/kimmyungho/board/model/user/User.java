package com.kimmyungho.board.model.user;

import com.kimmyungho.board.model.entity.UserEntity;

import java.time.ZonedDateTime;

public record User(Long userId,
                   String username,
                   String profile,
                   String description,
                   Long followerCount,
                   Long followingCount,
                   ZonedDateTime createdDateTime,
                   ZonedDateTime updatedDateTime,
                   ZonedDateTime deletedDateTime
                   ) {

    public static User from(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getProfile(),
                userEntity.getDescription(),
                userEntity.getFollowerCount(),
                userEntity.getFollowingCount(),
                userEntity.getCreatedDateTime(),
                userEntity.getUpdatedDateTime(),
                userEntity.getDeletedDateTime());
    }
}
