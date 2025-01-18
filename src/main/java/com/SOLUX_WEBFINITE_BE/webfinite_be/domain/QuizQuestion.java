package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(length = 255)
    private String questionContent;

    @Column(length = 100)
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    // QuizChoice와의 양방향 관계
    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    
    private List<QuizChoice> quizChoices = new ArrayList<>();

    // UserAnswer와의 양방향 관계
    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    // 연관 관계 메서드
    public void addQuizChoice(QuizChoice quizChoice) {
        quizChoices.add(quizChoice);
        quizChoice.setQuizQuestion(this);
    }

    public void addUserAnswer(UserAnswer userAnswer) {
        userAnswers.add(userAnswer);
        userAnswer.setQuizQuestion(this);
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}