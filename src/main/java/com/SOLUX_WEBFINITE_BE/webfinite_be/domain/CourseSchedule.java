package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter @Setter
public class CourseSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Day day;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // setter 직접 세팅
    // String -> Enum 타입인 Day로 변환
    public void setDay(String day) {
        switch (day) {
            case "MON":
                this.day = Day.MON; break;
            case "TUE":
                this.day = Day.TUE; break;
            case "WED":
                this.day = Day.WED; break;
            case "THU":
                this.day = Day.THU; break;
            case "FRI":
                this.day = Day.FRI; break;
            case "SAT":
                this.day = Day.SAT; break;
            case "SUN":
                this.day = Day.SUN; break;
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    // ==== 연관 관계 메서드 ====
    // 로직 상에서 양쪽에 값을 세팅해주기 위함
    public void setCourse(Course course){
        this.course = course;
        if (!course.getSchedules().contains(this)) {
            course.getSchedules().add(this);
        }
    }
}
