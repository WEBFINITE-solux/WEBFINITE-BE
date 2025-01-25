package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.LearningPlan;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanRepository extends JpaRepository<LearningPlan, Long> {

    // plan 조회
    @Query("select p from LearningPlan p where p.course.id = :id order by p.week asc")
    List<LearningPlan> findPlansByCourseId(@Param("id") Long courseId);

    // plan 삭제
    @Modifying
    @Query("delete from LearningPlan p where p.course.id = :id")
    void deletePlanByCourseId(@Param("id") Long courseId);
}
