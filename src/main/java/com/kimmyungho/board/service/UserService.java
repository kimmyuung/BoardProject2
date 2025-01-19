package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.user.UserAlreadyExistsException;
import com.kimmyungho.board.exception.user.UserNotAllowedException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.user.User;
import com.kimmyungho.board.model.user.UserAuthenticationResponse;
import com.kimmyungho.board.model.user.UserPatchRequestBody;
import com.kimmyungho.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired  private  UserEntityRepository userEntityRepository;
    @Autowired  private  BCryptPasswordEncoder passwordEncoder;
    @Autowired  private  JwtService jwtService;



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

    public List<User> getUsers(String query) {
     List<UserEntity> userEntities;

     if (query != null && !query.isBlank()) {
        userEntities = userEntityRepository.findByUsernameContaining(query);
     } else {
        userEntities = userEntityRepository.findAll();
     }
     return userEntities.stream().map(User::from).toList();
    }

    public User getUser(String username) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));

    return User.from(userEntity);
    }

    public User updateUser(
            String username, UserPatchRequestBody userPatchRequestBody, UserEntity cureentuser) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if(!userEntity.equals(cureentuser)) {
            throw new UserNotAllowedException();
        }
        if(userPatchRequestBody.description() != null) {
            userEntity.setDescription(userPatchRequestBody.description());
        }

        return User.from(userEntityRepository.save(userEntity));
    }
}
