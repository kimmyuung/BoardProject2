package com.kimmyungho.board.model.user;

import com.kimmyungho.board.model.entity.UserEntity;

import java.time.ZonedDateTime;

public record User(Long userId,
                   String username,
                   String profile,
                   String description,
                   Long followersCount,
                   Long followingsCount,
                   ZonedDateTime createdDateTime,
                   ZonedDateTime updatedDateTime,
                   ZonedDateTime deletedDateTime,
                   Boolean isFollowing
                   ) {

    public static User from(UserEntity userEntity) {
        return new User(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getProfile(),
                userEntity.getDescription(),
                userEntity.getFollowersCount(),
                userEntity.getFollowingsCount(),
                userEntity.getCreatedDateTime(),
                userEntity.getUpdatedDateTime(),
                userEntity.getDeletedDateTime(),
                null);
    }
    public static User from(UserEntity userEntity, Boolean isFollowing) {
        return new User(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getProfile(),
                userEntity.getDescription(),
                userEntity.getFollowersCount(),
                userEntity.getFollowingsCount(),
                userEntity.getCreatedDateTime(),
                userEntity.getUpdatedDateTime(),
                userEntity.getDeletedDateTime(),
                isFollowing);
    }
}
