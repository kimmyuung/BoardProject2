package com.kimmyungho.board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "reply",
indexes = { @Index(name = "reply_userid_idx", columnList = "userid"),
        @Index(name = "reply_postid_idx", columnList = "postid")})
@SQLDelete(sql = "UPDATE \"reply\" SET deleted_date_time = CURRENT_TIMESTAMP WHERE replyid = ?")
@SQLRestriction("deleted_date_time is NULL")
// @WHERE ->  해당 제약 조건 추가하는 조건절
// SOFT DELETE 서비스 상에서 보이지는 않으나 DB 상에는 존재 (조회 및 통계 용도로 사용)
@Getter@Setter@EqualsAndHashCode
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

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
        this.updatedDateTime = this.createdDateTime;

    }

    @PreUpdate
    private void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }

    public static ReplyEntity of(String body, UserEntity user, PostEntity post) {
        var reply = new ReplyEntity();
        reply.setBody(body);
        reply.setUser(user);
        reply.setPost(post);
        return reply;
    }
}
