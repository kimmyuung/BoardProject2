package com.kimmyungho.board.model;

import lombok.*;

import java.time.ZonedDateTime;
@Getter
@Setter@AllArgsConstructor
@ToString@EqualsAndHashCode

public class Post {
    private Long postId;

    private String body;

    private ZonedDateTime createdDateTime;
}
