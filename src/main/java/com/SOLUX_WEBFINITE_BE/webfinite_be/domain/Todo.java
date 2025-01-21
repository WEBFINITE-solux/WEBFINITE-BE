package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column(length = 100, nullable = true)
    private String todoContent;

    @Column(nullable = false)
    private Boolean isCompleted = false;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void setStartDate(LocalDateTime localDateTime) {
        this.startDate = localDateTime;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getUserId() {
        return this.user.getUserId();
    }

    // 연관 관계 메서드(User에 관련 설정 추가 필요함)
    public void setUser(User user) {
        this.user = user;
        if (!user.getTodos().contains(this)) {
            user.getTodos().add(this);
        }
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
