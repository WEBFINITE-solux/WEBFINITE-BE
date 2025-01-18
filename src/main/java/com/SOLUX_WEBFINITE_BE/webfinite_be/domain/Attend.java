package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;

@Entity
@Getter
public class Attend {

    @Id
    @GeneratedValue
    @Column(name = "attend_id")
    private Long id;

    @Column(name = "attend_date")
    private LocalDate attendDate;

    @Column(name = "is_attended", nullable = false)
    private boolean isAttended;

    @Column(name = "attend_date_cnt")
    private int attendDateCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // == 연관관계 메서드 == //
//    public void setMember(User user) {
//        this.user = user;
//        user.getUser().add(this);
//    }

}
