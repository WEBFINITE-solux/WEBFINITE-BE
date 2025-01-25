package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepositoryCustom {
    // 날짜 범위와 userId 기반 조회
    List<Todo> findTodosByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    // Todo 완료 상태 업데이트
    void updateTodoCompletion(Long todoId, boolean isCompleted);

    // Todo 삭제
    void deleteTodo(Long todoId);
}
