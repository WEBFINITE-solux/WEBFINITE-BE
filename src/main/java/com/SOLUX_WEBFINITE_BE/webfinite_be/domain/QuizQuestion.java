package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(length = 255, nullable = false)
    private String questionContent;

    @Column(length = 500, nullable = false)
    private String answer;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizChoice> quizChoices = new ArrayList<>();

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    public void addQuizChoice(QuizChoice quizChoice) {
        if (!quizChoices.contains(quizChoice)) {
            quizChoices.add(quizChoice);
            quizChoice.setQuizQuestion(this);
        }
    }

    public void addUserAnswer(UserAnswer userAnswer) {
        userAnswers.add(userAnswer);
        userAnswer.setQuizQuestion(this);
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        if (!quiz.getQuizQuestions().contains(this)) {
            quiz.getQuizQuestions().add(this);
        }
    }

}
