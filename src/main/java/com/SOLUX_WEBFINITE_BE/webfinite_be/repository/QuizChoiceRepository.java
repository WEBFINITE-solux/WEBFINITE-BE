package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizChoiceRepository extends JpaRepository<QuizChoice, Long> {
    // 특정 문제에 대한 선택지 조회
    List<QuizChoice> findByQuestionId(Long questionId);
}
