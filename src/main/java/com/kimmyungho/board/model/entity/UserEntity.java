package com.kimmyungho.board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "\"user\"")
@SQLDelete(sql = "UPDATE \"user\" SET deleted_date_time = CURRENT_TIMESTAMP WHERE post_id = ?")
@SQLRestriction("deleted_date_time is NULL")
@Getter@Setter@EqualsAndHashCode
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false) private String username;

    @Column(nullable = false) private String password;

    @Column private String profile;

    @Column private String description;

    @Column private ZonedDateTime createdDateTime;

    @Column private ZonedDateTime updatedDateTime;

    @Column private ZonedDateTime deletedDateTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 사용자별 권한 구분 처리시 필요
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static UserEntity of(String username, String password) {
        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);

        // Avatar Placeholder Service(https://avatar-placeholder.iran.irana.run) 기반
        // 랜덤한 프로필 사진 설정(1 ~ 100)
        userEntity.setProfile(
                "https://avatar.iran.liara.run/public/%d".formatted(
                        ThreadLocalRandom.current().nextInt(1, 101)
                )
        );

        // 위 API 미작동시
        // userEntity.setProfile(MessageFormat.format("https://dev-jayce.github.io/public/profile/{0}",
        //        new Random().nextInt(100) + ".png"));

        return userEntity;
    }

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
