package com.kimmyungho.board.repository;

import com.kimmyungho.board.model.entity.PostEntity;
import com.kimmyungho.board.model.entity.ReplyEntity;
import com.kimmyungho.board.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyEntityRepository extends JpaRepository<ReplyEntity, Long > {
    List<ReplyEntity> findByUser(UserEntity user);

    List<ReplyEntity> findByPost(PostEntity post);

    UserEntity user(UserEntity user);
}
