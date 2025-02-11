package com.kimmyungho.board.model.user;

import java.time.ZonedDateTime;

public record Follower(Long userId,
                     String username,
                     String profile,
                     String description,
                     Long followersCount,
                     Long followingsCount,
                     ZonedDateTime createdDateTime,
                     ZonedDateTime updatedDateTime,
                     ZonedDateTime followedDateTime,
                     Boolean isFollowing
                   ) {


    public static Follower from(User user, ZonedDateTime followedDateTime) {
        return new Follower(
                user.userId(),
                user.username(),
                user.profile(),
                user.description(),
                user.followersCount(),
                user.followersCount(),
                user.createdDateTime(),
                user.updatedDateTime(),
                followedDateTime,
                user.isFollowing());
    }
}
