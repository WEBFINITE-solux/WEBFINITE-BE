package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter // Setter 없어도 괜찮은 로직 찾으면 바꾸기
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
        if (!user.getCourses().contains(this)) { // 중복 추가 방지
            user.getCourses().add(this);
        }
    }

    public void addCourseSchedule(CourseSchedule schedule) {
        schedules.add(schedule);
        schedule.setCourse(this);

    }

    public void addFile(CourseFile file){
        files.add(file);
        file.setCourse(this);
    }

    public void setPrompt(Prompt prompt){
        this.prompt = prompt;
    }

    // ==== 생성 메서드 ====
    public static Course createCourse(User user, String title, LocalDate period, int year, int semester, String color, List<CourseSchedule> schedules){
        Course course = new Course();
        course.setUser(user);
        course.setTitle(title);
        course.setPeriod(period);
        course.setYear(year);
        course.setSemester(semester);
        course.setColor(color);

        for (CourseSchedule schedule : schedules){
            course.addCourseSchedule(schedule);
        }

        return course;
    }

    // 추가: 강의명 반환 메서드
    public String getCourseName() {
        return this.title;  // title 필드 값 반환
    }
}