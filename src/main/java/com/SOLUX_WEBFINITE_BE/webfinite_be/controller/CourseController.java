package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.FileDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SimpleResponse;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.CourseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{userId}/{year}/{semester}")
    public CourseListResponse courseList(@PathVariable("userId") Long id, @PathVariable("year") int year, @PathVariable("semester") int semester){
        List<Course> courses = courseService.getListThisSemester(id, year, semester);
        return new CourseListResponse(courses);
    }


    @GetMapping("/table/{userId}/{year}/{semester}")
    public CourseScheduleResponse courseTable(@PathVariable("userId") Long id, @PathVariable("year") int year, @PathVariable("semester") int semester){
        List<Map<String, Object>> courses = courseService.getCourseTimeTable(id, year, semester);
        return new CourseScheduleResponse(courses);
    }

    @PostMapping("/{userId}/new")
    public createCourseResponse saveCourse(@PathVariable("userId") Long id, @RequestBody @Valid createCourseRequest request){
        Long courseId = courseService.saveCourse(id, request.getTitle(), request.getPeriod(), request.getYear(), request.getSemester(), request.getColor(), request.toCourseSchedule());
        return new createCourseResponse(courseId);
    }

    @DeleteMapping("/{courseId}/delete")
    public SimpleResponse deleteCourse(@PathVariable("courseId") Long courseId){
        courseService.deleteCourse(courseId);
        return new SimpleResponse("강의 삭제 완료");
    }

    @GetMapping("/file/{courseId}")
    public FileListResponse getCourseFiles(@PathVariable("courseId") Long courseId){
        List<FileDTO> files = courseService.getCourseFiles(courseId);
        return new FileListResponse(files);
    }

    @PostMapping("/file/{courseId}/upload")
    public FileDTO uploadFile(@PathVariable("courseId") Long courseId, MultipartFile file){
        if(file.isEmpty() || file == null){
            throw new IllegalStateException("파일이 비어있습니다.");
        }
        // pdf, txt 파일만 업로드 가능
        if(!file.getContentType().equals("application/pdf") && !file.getContentType().equals("text/plain")){
            throw new IllegalStateException("pdf 파일 또는 txt 파일만 업로드 가능합니다.");
        }
        try {
            return courseService.uploadFile(courseId, file);
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.");
        }
    }

    @DeleteMapping("/file/{fileId}/delete")
    public SimpleResponse deleteFile(@PathVariable("fileId") Long fileId){
        courseService.deleteFile(fileId);
        return new SimpleResponse("강의 자료 삭제 완료");
    }

    // 사용자 ID를 기반으로 수강 중인 강의와 그에 해당하는 파일 리스트 조회
    @GetMapping("/{userId}/files")
    public ResponseEntity<List<Map<String, Object>>> getCourseFilesWithCourseName(@PathVariable Long userId) {
        List<Map<String, Object>> courseFiles = courseService.getCourseFilesWithCourseName(userId);
        return ResponseEntity.ok(courseFiles);
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

    @Data
    @AllArgsConstructor
    static class FileListResponse<T> {
        private T files;
    }
}