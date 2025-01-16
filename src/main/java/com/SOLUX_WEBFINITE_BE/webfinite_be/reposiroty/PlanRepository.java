package com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.LearningPlan;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlanRepository {
    private final EntityManager em;

    // prompt 저장
    public void savePrompt(Prompt prompt){
        em.persist(prompt);
    }

    // prompt 조회
    public Optional<Prompt> findPromptByCourseId(Long id){
        return Optional.ofNullable(em.find(Prompt.class, id));
    }

    // plan 저장
    public void savePlan(LearningPlan plan) {
        em.persist(plan);
    }

    // plan 조회
    public List<LearningPlan> findPlansByCourseId(Long id){
        return em.createQuery("select p from LearningPlan p where p.course.id = :id order by p.week asc", LearningPlan.class)
                .setParameter("id", id)
                .getResultList();
    }

    // plan 삭제
    public void deletePlan(Long courseId){
        em.createQuery("delete from LearningPlan p where p.course.id = :id")
                .setParameter("id", courseId)
                .executeUpdate();
    }
}
