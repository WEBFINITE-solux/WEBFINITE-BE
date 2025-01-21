package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Todo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Todo> findTodosByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery("SELECT t FROM Todo t WHERE t.user.id = :userId AND t.startDate >= :startDate AND t.endDate <= :endDate", Todo.class)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public void updateTodoCompletion(Long todoId, boolean isCompleted) {
        em.createQuery("UPDATE Todo t SET t.isCompleted = :isCompleted WHERE t.id = :todoId")
                .setParameter("isCompleted", isCompleted)
                .setParameter("todoId", todoId)
                .executeUpdate();
    }

    @Override
    public void deleteTodo(Long todoId) {
        em.createQuery("DELETE FROM Todo t WHERE t.id = :todoId")
                .setParameter("todoId", todoId)
                .executeUpdate();
    }
}
