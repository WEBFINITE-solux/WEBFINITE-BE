package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Quiz;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.QuestionDetailDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.QuizListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Quiz 엔티티를 반환하는 메서드
    @Query("SELECT q FROM Quiz q " +
            "LEFT JOIN FETCH q.quizQuestions qq " +
            "WHERE q.user.id = :userId AND q.course.id = :courseId")
    List<Quiz> findQuizzesByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);

    // 퀴즈 세부 정보 조회
    @Query("SELECT new com.SOLUX_WEBFINITE_BE.webfinite_be.dto.QuestionDetailDto(" +
            "qq.questionId, qq.questionContent, " +
            "(SELECT new com.SOLUX_WEBFINITE_BE.webfinite_be.dto.QuizChoiceDto(qc.choiceId, qc.choiceContent) " +
            "FROM QuizChoice qc WHERE qc.quizQuestion.questionId = qq.questionId ORDER BY qc.choiceId ASC), " +
            "qq.answer, qq.explanation) " +
            "FROM Quiz q " +
            "JOIN QuizQuestion qq ON q.quizId = qq.quiz.quizId " +
            "JOIN QuizChoice qc ON qc.quizQuestion.questionId = qq.questionId " +  // QuizChoice와 JOIN 추가
            "WHERE q.quizId = :quizId")
    List<QuestionDetailDto> findQuizDetailsById(@Param("quizId") Long quizId);



}


