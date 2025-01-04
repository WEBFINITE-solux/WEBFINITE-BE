package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_title", nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate period;

    @Column(name = "course_year",nullable = false)
    private int year;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<LearningPlan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseFile> files = new ArrayList<>();

    @OneToOne(mappedBy = "course",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Prompt prompt;

    // ==== 연관 관계 메서드 ====
    // 로직 상에서 양쪽에 값을 세팅해주기 위함
    public void setUser(User user){
        this.user = user;
        user.getCourses().add(this);
    }

    public void setPrompt(Prompt prompt){
        this.prompt = prompt;
    }
}
