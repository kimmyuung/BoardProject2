package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.user.UserAlreadyExistsException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.user.User;
import com.kimmyungho.board.model.user.UserAuthenticationResponse;
import com.kimmyungho.board.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserEntityRepository userEntityRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userEntityRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public User signUp(String username, String password) {
         userEntityRepository
                .findByUsername(username)
                 .ifPresent(user -> {
                     throw new UserAlreadyExistsException();
                 });

         var userEntity =
          userEntityRepository.save(UserEntity.of(username, passwordEncoder.encode(password)));

         return User.from(userEntity);
    }

    public UserAuthenticationResponse authenticate(String username, String password) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
          var accessToken = jwtService.generateAccessToken(userEntity);
          return new UserAuthenticationResponse(accessToken);
        } else {
            throw new UserNotFoundException();
        }
    }
}
