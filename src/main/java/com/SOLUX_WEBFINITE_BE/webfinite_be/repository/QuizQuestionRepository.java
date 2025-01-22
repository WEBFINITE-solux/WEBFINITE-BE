package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Quiz;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizId(Long quizId); // 특정 퀴즈의 질문들 가져오기

    List<QuizQuestion> findByQuiz(Quiz quiz);

}
