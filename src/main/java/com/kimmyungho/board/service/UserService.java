package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.follow.FollowAlreadyExistException;
import com.kimmyungho.board.exception.follow.FollowNotFoundException;
import com.kimmyungho.board.exception.follow.InvalidFollowException;
import com.kimmyungho.board.exception.user.UserAlreadyExistsException;
import com.kimmyungho.board.exception.user.UserNotAllowedException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.FollowEntity;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.user.User;
import com.kimmyungho.board.model.user.UserAuthenticationResponse;
import com.kimmyungho.board.model.user.UserPatchRequestBody;
import com.kimmyungho.board.repository.FollowEntityRepository;
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
    @Autowired  private  FollowEntityRepository followEntityRepository;



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

    public User follow(String username, UserEntity currentUser) {
        var following =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if(following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot follow themselves.");
        }

        followEntityRepository.findByFollowerAndFollowing(currentUser, following)
                .ifPresent(
                        follow -> {
                            throw new FollowAlreadyExistException(currentUser, following);
                        });
        followEntityRepository.save(FollowEntity.of(currentUser, following));

        following.setFollowerCount(following.getFollowerCount() + 1);
        currentUser.setFollowingCount(following.getFollowingCount() + 1);

        userEntityRepository.saveAll(List.of(following, currentUser));

        return User.from(following);
    }

    public User unfollow(String username, UserEntity currentUser) {
        var following =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if(following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot unfollow themselves.");
        }

        followEntityRepository
                .findByFollowerAndFollowing(currentUser, following)
                .orElseThrow(
                        () -> new FollowNotFoundException(currentUser, following)
                        );
        followEntityRepository.save(FollowEntity.of(currentUser, following));

        following.setFollowerCount(Math.max(0, following.getFollowerCount() - 1));
        currentUser.setFollowingCount(Math.max(0, following.getFollowingCount() - 1));

        userEntityRepository.saveAll(List.of(following, currentUser));

        return User.from(following);
    }
}
