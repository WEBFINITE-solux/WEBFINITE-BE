package com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty;

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
    public Optional<Course> findOne(Long id){
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

    // 강의 스케줄 조회 => 시간대 중복 체크용, 년도 및 학기 추가
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
}
