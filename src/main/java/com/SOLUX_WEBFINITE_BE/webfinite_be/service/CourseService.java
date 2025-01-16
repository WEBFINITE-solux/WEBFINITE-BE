package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.FileDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.CourseNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.UserNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // 강의 등록
    @Transactional
    public Long saveCourse(Long id, String title, LocalDate period, int year, int semester, String color, List<CourseSchedule> schedules){
        // 임시로 작성한 UserRepository 사용, 이후 UserRepository에서 꺼내오도록 수정 필요
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        // 시간대 유효성 검증
        validateSchedule(schedules);

        // 시간대 중복 체크
        validateScheduleConflict(user.getId(), schedules, year, semester);


        Course course = Course.createCourse(user, title, period, year, semester, color, schedules);

        courseRepository.save(course);
        return course.getId();
    }

    // 강의 목록 조회(시간X)
    public List<Course> getListThisSemester(Long id, int year, int semester){
        // 임시로 작성한 UserRepository 사용, 이후 UserRepository에서 꺼내오도록 수정 필요
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        return courseRepository.findThisSemester(user.getId(), year, semester);
    }

    private void validateSchedule(List<CourseSchedule> schedules) {
        for (CourseSchedule schedule : schedules) {
            if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
                throw new IllegalStateException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
            }
        }
    }

    // 강의 시간표 조회
    public List<Map<String, Object>> getCourseTimeTable(Long id, int year, int semester){
        List<Object[]> results = courseRepository.findCourseWithSchedules(id, year, semester);

        // 결과 가공
        Map<Long, Map<String, Object>> courseMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Course course = (Course) row[0];
            CourseSchedule schedule = (CourseSchedule) row[1];


            // 강의 데이터 추가
            courseMap.putIfAbsent(course.getId(), Map.of(
                    "course_id", course.getId(),
                    "title", course.getTitle(),
                    "color", course.getColor(),
                    "schedule", new ArrayList<>()
            ));

            // 각 강의에 대한 스케줄 추가
            List<Map<String, Object>> schedules = (List<Map<String, Object>>) courseMap.get(course.getId()).get("schedule");
            schedules.add(Map.of(
                    "day", schedule.getDay().toString(),
                    "start_time", schedule.getStartTime().toString(),
                    "end_time", schedule.getEndTime().toString(),
                    "location", schedule.getLocation()
            ));
        }
        return new ArrayList<>(courseMap.values());
    }

    // 강의 삭제
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException());

        courseRepository.delete(course);
    }

    // 파일 업로드
    public FileDTO uploadFile(Long courseId,  MultipartFile file) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException());

        if(file.isEmpty())
            throw new IllegalStateException("파일이 비어있습니다.");

        String fileName = file.getOriginalFilename();

        // 파일 저장 경로 설정
        String filepath = "uploads/"+ UUID.randomUUID()+"_"+fileName;

        // 파일 저장 (개발 진행 -> 로컬, 배포 -> S3(예정))
        Path path = Paths.get(filepath);
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, file.getBytes());

        // CourseFile 엔티티 생성
        CourseFile courseFile = new CourseFile();
        courseFile.setOriginalFilename(fileName);
        courseFile.setFilePath(filepath);
        courseFile.setCourse(course); // 연관 관계 설정

        course.addFile(courseFile);

        courseRepository.save(courseFile);

        return new FileDTO( courseFile.getId(), courseFile.getOriginalFilename());
    }

    // 강의 자료 조회
    public List<FileDTO> getCourseFiles(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException());

        List<CourseFile> files = courseRepository.findFilesByCourseId(courseId);

        return files.stream()
                .map(file -> new FileDTO(file.getId(), file.getOriginalFilename()))
                .toList();
    }

    // 강의 자료 삭제
    public void deleteFile(Long fileId) {
        CourseFile file = courseRepository.findFileById(fileId).orElseThrow(() -> new IllegalStateException("파일이 존재하지 않습니다."));
        courseRepository.delete(file);
    }

    // 시간대 중복 체크
    private void validateScheduleConflict(Long userId, List<CourseSchedule> newSchedules, int year, int semester) {
        List<CourseSchedule> existingSchedules = courseRepository.findScheduleByUserId(userId, year, semester);

        for(CourseSchedule existingSchedule : existingSchedules){
            for(CourseSchedule newSchedule : newSchedules){
                if(existingSchedule.getDay().equals(newSchedule.getDay())){
                    if(isTimeOverlap(existingSchedule, newSchedule)){
                        throw new IllegalStateException("해당 시간대에 이미 강의가 존재합니다.");
                    }
                }
            }
        }
    }

    private boolean isTimeOverlap(CourseSchedule existingSchedule, CourseSchedule newSchedule) {
        // 완전히 겹치는 경우
        if (newSchedule.getStartTime().equals(existingSchedule.getStartTime()) &&
                newSchedule.getEndTime().equals(existingSchedule.getEndTime())) {
            return true; // 중복
        }

        // 종료 시간이 기존 강의의 시작 시간과 동일하거나
        // 시작 시간이 기존 강의의 종료 시간과 동일한 경우 중복 아님
        if (newSchedule.getEndTime().equals(existingSchedule.getStartTime()) ||
                newSchedule.getStartTime().equals(existingSchedule.getEndTime())) {
            return false; // 중복 아님
        }

        // 일반적인 겹침 조건
        return (newSchedule.getStartTime().isBefore(existingSchedule.getEndTime()) &&
                newSchedule.getEndTime().isAfter(existingSchedule.getStartTime()));
    }

}
