package com.kimmyungho.board.exception.follow;

import com.kimmyungho.board.exception.ClientErrorException;
import com.kimmyungho.board.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends ClientErrorException {

    public FollowNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Follow not found");
    }

    public FollowNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public FollowNotFoundException(UserEntity follower, UserEntity following) {
        super(HttpStatus.NOT_FOUND, "Follow with follower " + follower.getUsername()
                + " and following "
                + following.getUsername()
                + " already exists");
    }
}
