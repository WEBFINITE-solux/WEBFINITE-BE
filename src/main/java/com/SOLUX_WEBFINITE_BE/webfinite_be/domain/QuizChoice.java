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

    // 기본 생성자 추가
    public QuizChoice() {
    }

    // choiceContent를 설정할 수 있는 생성자 추가
    public QuizChoice(String choiceContent) {
        this.choiceContent = choiceContent;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
        if (quizQuestion != null && !quizQuestion.getQuizChoices().contains(this)) {
            quizQuestion.getQuizChoices().add(this);
        }
    }

    // choiceContent 설정을 위한 메서드 추가
    public void setChoiceContent(String choiceContent) {
        this.choiceContent = choiceContent;
    }
}