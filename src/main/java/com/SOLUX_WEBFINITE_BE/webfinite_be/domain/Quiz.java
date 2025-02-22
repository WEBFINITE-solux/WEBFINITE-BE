package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @Column(length = 100, nullable = false)
    private String quizTitle;

    @Enumerated(EnumType.STRING)
    private QuizState quizState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private CourseFile courseFile;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 필드 이름을 명확히 "course"로 설정

    @Enumerated(EnumType.STRING) // Enum 타입 저장
    private QuestionType quizType; // 퀴즈 유형

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAnswer> userAnswers = new ArrayList<>();  // 추가된 부분

    public void addQuizQuestion(QuizQuestion quizQuestion) {
        quizQuestions.add(quizQuestion);
        quizQuestion.setQuiz(this);
    }

    public String getCourseName() {
        return course != null ? course.getTitle() : null; // Course가 null이 아니면 title 반환
    }

    @PrePersist
    public void prePersist() {
        if (quizState == null) {
            quizState = QuizState.IN_PROGRESS;
        }
    }
}
