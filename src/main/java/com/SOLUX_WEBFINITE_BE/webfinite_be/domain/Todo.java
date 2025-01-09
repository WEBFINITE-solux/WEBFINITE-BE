package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column(length = 100, nullable = true)
    private String todoContent;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==== 연관 관계 메서드 ====
    //public void setUser(User user) {
    //    this.user = user;
    //    user.getTodos().add(this);
    //}
}
