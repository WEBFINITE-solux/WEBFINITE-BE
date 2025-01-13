package com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlanRepository {
    private final EntityManager em;

    // prompt 저장
    public void savePrompt(Prompt prompt){
        em.persist(prompt);
    }
}
