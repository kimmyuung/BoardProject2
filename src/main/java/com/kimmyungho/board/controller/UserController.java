package com.kimmyungho.board.controller;

import com.kimmyungho.board.model.user.User;
import com.kimmyungho.board.model.user.UserAuthenticationResponse;
import com.kimmyungho.board.model.user.UserLoginRequestBody;
import com.kimmyungho.board.model.user.UserSignUpRequestBody;
import com.kimmyungho.board.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    //@Autowired UserService userService;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody userSignUpRequestBody) {
        var user = userService.signUp(
                userSignUpRequestBody.username(),
                userSignUpRequestBody.password()
        );
        return ResponseEntity.ok(user);
       // return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(
            @Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        var response = userService.authenticate(userLoginRequestBody.username(), userLoginRequestBody.password());
        return ResponseEntity.ok(response);
        // return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
