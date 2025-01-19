package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 강의 리스트 조회 => 해당 학기 강의 조회, 시간표XXX
    List<Course> findByUserIdAndYearAndSemester(@Param("id") Long id, @Param("year") int year, @Param("semester") int semester);

    // 강의 시간표 조회, 강의 목록+일정
    @Query("select c, cs from Course c join c.schedules cs where c.user.id = :id and c.year = :year and c.semester = :semester")
    List<Object[]> findCourseWithSchedules(@Param("id") Long id, @Param("year") int year, @Param("semester") int semester);

    // 강의 일정 조회 => 시간대 중복 체크용, only 일정
    @Query("select cs from CourseSchedule cs join cs.course c where c.user.id = :id and c.year = :year and c.semester = :semester")
    List<CourseSchedule> findScheduleByUserId(@Param("id") Long userId, @Param("year") int year, @Param("semester") int semester);
}
