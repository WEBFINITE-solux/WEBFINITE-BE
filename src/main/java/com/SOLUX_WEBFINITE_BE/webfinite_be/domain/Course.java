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

    @OneToMany(mappedBy = "course")
    private List<CourseSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<LearningPlan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<CourseFile> files = new ArrayList<>();

    @OneToOne(mappedBy = "course",fetch = FetchType.LAZY)
    private Prompt prompt;
}
