package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {
    // 기본적인 CRUD 메서드는 JpaRepository에서 제공됨

    // userId로 Todo 조회
    List<Todo> findByUser_Id(Long userId);
}
