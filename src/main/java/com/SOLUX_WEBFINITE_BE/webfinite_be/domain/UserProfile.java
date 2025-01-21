package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserProfile {

    @Id
    @GeneratedValue
    @Column(name = "profile_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "icon")
    private Icon icon = Icon.img1; // 기본값 설정

    @Column(name = "introduction")
    private String introduction = "오늘도 열심히!"; // 기본값 설정

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // == 연관관계 메서드 == //
    // 프로필 사진 변경
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    // == 비즈니스 메서드 == //
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

}
