package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class QuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long choiceId;

    @Column(length = 100, nullable = false)
    private String choiceContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

    // 연관 관계 메서드
    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
        if (!quizQuestion.getQuizChoices().contains(this)) {
            quizQuestion.getQuizChoices().add(this);
        }
    }

}
