package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.UserNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
        User user = userRepository.findOne(id)
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
    public List<Course> findListThisSemester(Long id, int year, int semester){
        // 임시로 작성한 UserRepository 사용, 이후 UserRepository에서 꺼내오도록 수정 필요
        User user = userRepository.findOne(id)
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
