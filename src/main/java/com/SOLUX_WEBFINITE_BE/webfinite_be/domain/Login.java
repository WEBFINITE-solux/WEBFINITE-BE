package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter

public class Login {

    @Id
    @GeneratedValue
    @Column(name = "login_id")
    private Long id;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attend_id")
    private Attend attend;

    // == 연관관계 메서드 == //
//    public void setMember(Attend attend) {
//        this.attend = attend;
//        user.getAttend().add(this);
//    }

}
