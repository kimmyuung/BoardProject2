package com.kimmyungho.board.model.user;

import java.time.ZonedDateTime;

public record LikedUser(Long userId,
                        String username,
                        String profile,
                        String description,
                        Long followersCount,
                        Long followingsCount,
                        ZonedDateTime createdDateTime,
                        ZonedDateTime updatedDateTime,
                        Boolean isFollowing,
                        Long likedPostId,
                        ZonedDateTime likedDateTime
                   ) {


    public static LikedUser from(User user, Long likePostId, ZonedDateTime likeedDateTime) {
        return new LikedUser(
                user.userId(),
                user.username(),
                user.profile(),
                user.description(),
                user.followersCount(),
                user.followersCount(),
                user.createdDateTime(),
                user.updatedDateTime(),
                user.isFollowing(),
                likePostId,
                likeedDateTime
                );
    }
}
