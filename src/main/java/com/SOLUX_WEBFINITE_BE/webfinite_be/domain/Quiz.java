package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @Column(length = 100)
    private String quizTitle;

    @Enumerated(EnumType.STRING)
    private QuizState quizStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ==== 연관 관계 메서드 ====
    //public void setUser(User user) {
    //    this.user = user;
    //    user.getQuizzes().add(this);
    //}




}
