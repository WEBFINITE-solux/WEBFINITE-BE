package com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    // prompt 조회
    Optional<Prompt> findByCourseId(Long courseId);
}