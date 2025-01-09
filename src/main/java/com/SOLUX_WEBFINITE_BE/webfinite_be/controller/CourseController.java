package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.FileDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.CourseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{userId}")
    public CourseListResponse courseList(@PathVariable("userId") Long id, @RequestBody @Valid CourseListRequest request){
        List<Course> courses = courseService.getListThisSemester(id, request.getYear(), request.getSemester());
        return new CourseListResponse(courses);
    }


    @GetMapping("/table/{userId}")
    public CourseScheduleResponse courseTable(@PathVariable("userId") Long id, @RequestBody @Valid CourseListRequest request){
        List<Map<String, Object>> courses = courseService.getCourseTimeTable(id, request.getYear(), request.getSemester());
        return new CourseScheduleResponse(courses);
    }

    @PostMapping("/{userId}/new")
    public createCourseResponse saveCourse(@PathVariable("userId") Long userId, @RequestBody @Valid createCourseRequest request){

        Long courseId = courseService.saveCourse(userId, request.getTitle(), request.getPeriod(), request.getYear(), request.getSemester(), request.getColor(), request.toCourseSchedule());
        return new createCourseResponse(courseId);
    }

    @PostMapping("/file/{courseId}/upload")
    public FileDTO uploadFile(@PathVariable("courseId") Long courseId, MultipartFile file){
        try {
            return courseService.uploadFile(courseId, file);
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실해했습니다.");
        }
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

    @Data
    static class CourseListResponse{
        private List<CourseListDTO> courses;

        public CourseListResponse(List<Course> courses){
            this.courses = courses.stream()
                    .map(c -> new CourseListDTO(c.getId(), c.getTitle(), c.getPeriod()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @Getter
    @AllArgsConstructor
    static class CourseListRequest{
        private int year;
        private int semester;
    }

    @Data
    @AllArgsConstructor
    static class CourseListDTO {
        private Long id;
        private String title;
        private LocalDate period;
    }

    @Data
    @AllArgsConstructor
    static class CourseScheduleResponse<T>{
        private T courses;
    }
}
