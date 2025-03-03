package com.kimmyungho.board.exception.reply;

import com.kimmyungho.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ClientErrorException {

    public ReplyNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Post not found");
    }

    public ReplyNotFoundException(Long replyId) {
        super(HttpStatus.NOT_FOUND, "Post with replyId " + replyId + " not found");
    }

    public ReplyNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
