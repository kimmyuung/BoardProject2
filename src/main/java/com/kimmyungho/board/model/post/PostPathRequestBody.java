package com.kimmyungho.board.model.post;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter@EqualsAndHashCode

// public record PostPostRequestBody(String body) {}

public class PostPathRequestBody {
    private String body;
}
