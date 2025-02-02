package com.kimmyungho.board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(
        name = "\"like\"",
        indexes =
                { @Index(name = "like_userid_postid_idx", columnList = "userid, postid", unique = true)})
@Getter@Setter@EqualsAndHashCode
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column
    private ZonedDateTime createdDateTime;

    // TODO : 유저 정보 필요
    @ManyToOne
    @JoinColumn(name = "userid")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "postid")
    private PostEntity post;

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
    }

    public static LikeEntity of(UserEntity user, PostEntity post) {
        var likeEntity = new LikeEntity();
        likeEntity.setUser(user);
        likeEntity.setPost(post);
        return likeEntity;
    }
}
