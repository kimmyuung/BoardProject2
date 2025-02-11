package com.kimmyungho.board.service;

import com.kimmyungho.board.exception.follow.FollowAlreadyExistException;
import com.kimmyungho.board.exception.follow.FollowNotFoundException;
import com.kimmyungho.board.exception.follow.InvalidFollowException;
import com.kimmyungho.board.exception.post.PostNotFoundException;
import com.kimmyungho.board.exception.user.UserAlreadyExistsException;
import com.kimmyungho.board.exception.user.UserNotAllowedException;
import com.kimmyungho.board.exception.user.UserNotFoundException;
import com.kimmyungho.board.model.entity.FollowEntity;
import com.kimmyungho.board.model.entity.LikeEntity;
import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.model.entity.UserEntity;
import com.kimmyungho.board.model.user.*;
import com.kimmyungho.board.repository.FollowEntityRepository;
import com.kimmyungho.board.repository.LikeEntityRepository;
import com.kimmyungho.board.repository.PostEntityRepository;
import com.kimmyungho.board.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired  private  UserEntityRepository userEntityRepository;
    @Autowired  private  BCryptPasswordEncoder passwordEncoder;
    @Autowired  private  JwtService jwtService;
    @Autowired  private  FollowEntityRepository followEntityRepository;
    @Autowired  private  PostEntityRepository postEntityRepository;
    @Autowired  private LikeEntityRepository likeEntityRepository;



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

    public List<User> getUsers(String query, UserEntity currentUser) {
     List<UserEntity> userEntities;

     if (query != null && !query.isBlank()) {
        userEntities = userEntityRepository.findByUsernameContaining(query);
     } else {
        userEntities = userEntityRepository.findAll();
     }
     return userEntities.stream()
             .map(userEntity -> getUserWithFollowingStatus(userEntity, currentUser))
             .toList();
    }

    public User getUser(String username, UserEntity currentUser) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));

    return getUserWithFollowingStatus(userEntity, currentUser);
    }

    private User getUserWithFollowingStatus(UserEntity userEntity, UserEntity currentUser) {
        var isFollowing =
                followEntityRepository.findByFollowerAndFollowing(currentUser, userEntity).isPresent();
        return User.from(userEntity, isFollowing);
    }

    public User updateUser(
            String username, UserPatchRequestBody userPatchRequestBody, UserEntity currentUser) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if(!userEntity.equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        if(userPatchRequestBody.description() != null) {
            userEntity.setDescription(userPatchRequestBody.description());
        }

        return User.from(userEntityRepository.save(userEntity));
    }

    @Transactional
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

        following.setFollowersCount(following.getFollowersCount() + 1);
        currentUser.setFollowingsCount(following.getFollowingsCount() + 1);

        userEntityRepository.saveAll(List.of(following, currentUser));

        return User.from(following, true);
    }
    @Transactional
    public User unfollow(String username, UserEntity currentUser) {
        var following =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        if(following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot unfollow themselves.");
        }

        var followEntity = followEntityRepository
                .findByFollowerAndFollowing(currentUser, following)
                .orElseThrow(
                        () -> new FollowNotFoundException(currentUser, following)
                        );
        followEntityRepository.delete(followEntity);

        following.setFollowersCount(Math.max(0, following.getFollowersCount() - 1));
        currentUser.setFollowingsCount(Math.max(0, following.getFollowingsCount() - 1));

        userEntityRepository.saveAll(List.of(following, currentUser));

        return User.from(following, false);
    }

    public List<Follower> getFollowersByUsername(String username, UserEntity currentUser) { // username이 팔로잉
        var following =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        var followEntities = followEntityRepository.findByFollower(following);
        return followEntities.stream().map(
                follow -> Follower.from(
                        getUserWithFollowingStatus(follow.getFollower(), currentUser),
                        follow.getCreatedDateTime()
                        ))
                .toList();
    }

    public List<User> getFollowingsByUsername(String username, UserEntity currentUser) { // username이 팔로워
        var follower =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        var followEntities = followEntityRepository.findByFollower(follower);
        return followEntities.stream().map(follow -> getUserWithFollowingStatus(follow.getFollowing(), currentUser))
                .toList();
    }

    public List<LikedUser> getLikedUsersByPostId(Long postId, UserEntity currentUser) {
        var postEntity =
                postEntityRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

        var likeEntities = likeEntityRepository.findByPost(postEntity);
        return likeEntities.stream()
                .map(likeEntity ->
                    getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser))
        .toList();
    }

    private LikedUser getLikedUserWithFollowingStatus(
            LikeEntity likeEntity, PostEntity postEntity, UserEntity currentUser) {
        var likedUserEntity = likeEntity.getUser();
        var userWithFollowingStatus =
                getUserWithFollowingStatus(likedUserEntity, currentUser);
        return LikedUser.from(
                userWithFollowingStatus, postEntity.getPostId(), likeEntity.getCreatedDateTime());
    }

    public List<LikedUser> getLikedByUser(String username, UserEntity currentUser) {
        var userEntity =
                userEntityRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));
        var postEntities = postEntityRepository.findByUser(userEntity);

        return postEntities.stream()
                .flatMap(
                postEntity ->
            likeEntityRepository.findByPost(postEntity).stream()
                    .map(
                    likeEntity ->
                            getLikedUserWithFollowingStatus(likeEntity, postEntity, currentUser)))
        .toList();
    }
}
