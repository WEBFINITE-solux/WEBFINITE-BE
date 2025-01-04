package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class LearningPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(nullable = false)
    private int week;

    @Column(name = "plan_title", nullable = false)
    private String title;

    @Column(name = "plan_description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // ==== 연관 관계 메서드 ====
    // 로직 상에서 양쪽에 값을 세팅해주기 위함
    public void setCourse(Course course){
        this.course = course;
        if (!course.getPlans().contains(this)) {
            course.getPlans().add(this);
        }
    }
}
