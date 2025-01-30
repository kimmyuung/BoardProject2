package com.kimmyungho.board.model.reply;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter@EqualsAndHashCode

// public record PostPostRequestBody(String body) {}

public class ReplyPostRequestBody {
    private String body;
}
