package com.kimmyungho.board.exception.follow;

import com.kimmyungho.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class InvalidFollowException  extends ClientErrorException{

        public InvalidFollowException() {
            super(HttpStatus.BAD_REQUEST, "Invalid follow request.");
        }

        public InvalidFollowException(String message) {
            super(HttpStatus.BAD_REQUEST, message);
        }
}
