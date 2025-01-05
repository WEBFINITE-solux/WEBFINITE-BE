package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Day;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.CourseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    @PostMapping("/{userId}")
    public createCourseResponse saveCourse(@PathVariable Long userId, @RequestBody @Valid CourseController.createCourseRequest request){

        Long courseId = courseService.saveCourse(userId, request.getTitle(), request.getPeriod(), request.getYear(), request.getSemester(), request.getColor(), request.toCourseSchedule());
        return new createCourseResponse(courseId);
    }

    @Data
    @AllArgsConstructor
    private static class createCourseResponse {
        private Long courseId;
    }

    @Data
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    private static class createCourseRequest {
        private String title;
        private LocalDate period;
        private int year;
        private int semester;
        private String color;
        private List<String> day;
        @JsonProperty("start_time")
        private LocalTime startTime;
        @JsonProperty("end_time")
        private LocalTime endTime;
        private String location;

        public List<CourseSchedule> toCourseSchedule() {
            List<CourseSchedule> schedules = day.stream().map(d -> {
                CourseSchedule schedule = new CourseSchedule();
                schedule.setDay(d);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setLocation(location);
                return schedule;
            }).collect(Collectors.toList());
            return schedules;
        }
    }
}
