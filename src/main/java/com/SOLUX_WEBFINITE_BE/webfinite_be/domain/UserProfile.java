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
    private Icon icon;

    @Column(name = "introduction")
    private String introduction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // == 연관관계 메서드 == //
//    public void setMember(User user) {
//        this.user = user;
//        user.getUser().add(this);
//    }

    // == 비즈니스 메서드 == //
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

}
