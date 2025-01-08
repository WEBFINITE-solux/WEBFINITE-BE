package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz_choice")
public class QuizChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long choiceId;

    @Column(length = 100, nullable = false)
    private String choiceContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

}
