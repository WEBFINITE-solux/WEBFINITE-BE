package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(length = 255)
    private String userAnswer;

    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연관 관계 메서드
    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
        if (!quizQuestion.getUserAnswers().contains(this)) {
            quizQuestion.getUserAnswers().add(this);
        }
    }

    // 연관 관계 메서드(User에 관련 설정 추가 필요함)
    //public void setUser(User user) {
    //    this.user = user;
    //    if (!user.getUserAnswers().contains(this)) {
    //        user.getUserAnswers().add(this);
    //    }
    //}
}
