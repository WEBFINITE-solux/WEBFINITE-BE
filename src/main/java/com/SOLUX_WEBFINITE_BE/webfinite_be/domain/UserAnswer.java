package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(length = 255, nullable = false)
    private String userAnswer;

    private boolean isCorrect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
        if (!quizQuestion.getUserAnswers().contains(this)) {
            quizQuestion.getUserAnswers().add(this);
        }
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getUserAnswers().contains(this)) {
            user.getUserAnswers().add(this);
        }
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        if (quiz != null && !quiz.getUserAnswers().contains(this)) {
            quiz.getUserAnswers().add(this);
        }
    }

    public boolean getIsCorrect() {
        return this.isCorrect;
    }
}
