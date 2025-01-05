package com.kimmyungho.board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE \"post\" SET deleted_date_time = CURRENT_TIMESTAMP WHERE post_id = ?")
@SQLRestriction("deleted_date_time is NULL")
// @WHERE ->  해당 제약 조건 추가하는 조건절
// SOFT DELETE 서비스 상에서 보이지는 않으나 DB 상에는 존재 (조회 및 통계 용도로 사용)
@Getter@Setter@EqualsAndHashCode
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
        this.updatedDateTime = this.createdDateTime;

    }

    @PreUpdate
    private void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }
}
