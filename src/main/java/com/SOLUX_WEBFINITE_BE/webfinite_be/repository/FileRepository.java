package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<CourseFile, Long> {
    List<CourseFile> findByCourseId(Long courseId);
}
