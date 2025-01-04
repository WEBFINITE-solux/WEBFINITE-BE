package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_unit", nullable = false)
    private LocalDate startUnit;

    @Column(name = "end_unit", nullable = false)
    private LocalDate endUnit;

    @Column(name = "prompt_description", columnDefinition = "TEXT")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // ==== 연관 관계 메서드 ====
    // 로직 상에서 양쪽에 값을 세팅해주기 위함
    public void setCourse(Course course){
        this.course = course;
        course.setPrompt(this);
    }
}
