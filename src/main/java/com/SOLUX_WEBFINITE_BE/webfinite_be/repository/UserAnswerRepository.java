package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Quiz;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizQuestion;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByQuiz_QuizId(Long quizId); // 특정 퀴즈의 답변 가져오기

    // 퀴즈, 질문, 사용자 기준으로 답안을 찾는 메서드 추가
    Optional<UserAnswer> findByQuizQuestionAndQuizAndUser_Id(QuizQuestion quizQuestion, Quiz quiz, Long userId);

    List<UserAnswer> findByQuiz(Quiz quiz);

    boolean existsByQuizAndUser(Quiz quiz, User user);
}
