package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseSchedule;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseRepository {
    private final EntityManager em;

    // 강의 저장
    public void save(Course course){
        em.persist(course);
    }

    // 강의 조회
    public Optional<Course> findById(Long id){
        return Optional.ofNullable(em.find(Course.class, id));
    }

    // 강의 리스트 조회 => 해당 학기 강의 조회, 시간표XXX
    public List<Course> findThisSemester(Long id, int year, int semester){
        // (JPQL, 반환 타입)
        return em.createQuery("select c from Course c where c.user.id = :id and c.year = :year and c.semester = :semester", Course.class)
                .setParameter("id", id)
                .setParameter("year", year)
                .setParameter("semester", semester)
                .getResultList();
    }

    // 강의 시간표 조회, 강의 목록+일정
    public List<Object[]> findCourseWithSchedules(Long id, int year, int semester){
        return em.createQuery("" +
                        "select c, cs from Course c JOIN c.schedules cs " +
                        "where c.user.id = :id and c.year = :year and c.semester = :semester", Object[].class)
                .setParameter("id", id)
                .setParameter("year", year)
                .setParameter("semester", semester)
                .getResultList();
    }

    // 강의 일정 조회 => 시간대 중복 체크용, only 일정
    public List<CourseSchedule> findScheduleByUserId(Long userId, int year, int semester){
        return em.createQuery(
                        "select cs from CourseSchedule cs, Course c " +
                                "where c.user.id = :id " +
                                "and c.year = :year " +
                                "and c.semester = :semester " +
                                "and cs.course.id = c.id",
                        CourseSchedule.class)
                .setParameter("id", userId)
                .setParameter("year", year)
                .setParameter("semester", semester)
                .getResultList();
    }

    // 강의 삭제
    public void delete(Course course) {
        em.remove(course);
    }

    // 파일 저장
    public void save(CourseFile courseFile){
        em.persist(courseFile);
    }

    // 파일 조회
    public List<CourseFile> findFilesByCourseId(Long courseId){
        return em.createQuery("select cf from CourseFile cf where cf.course.id = :id", CourseFile.class)
                .setParameter("id", courseId)
                .getResultList();
    }

    // CourseRepository에 findFileById 메서드 추가
    public Optional<CourseFile> findFileById(Long fileId) {
        return em.createQuery("select cf from CourseFile cf where cf.id = :fileId", CourseFile.class)
                .setParameter("fileId", fileId)
                .getResultStream()
                .findFirst();
    }

    // 사용자 ID를 기준으로 수강 중인 강의 리스트 조회
    public List<Course> findByCourse_UserId(Long userId) {
        return em.createQuery("select c from Course c where c.user.id = :userId", Course.class)
                .setParameter("userId", userId)
                .getResultList();
    }



}