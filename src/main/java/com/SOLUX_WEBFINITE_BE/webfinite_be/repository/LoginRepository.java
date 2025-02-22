package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {


    @Query("SELECT l FROM Login l WHERE l.attend.user.id = :userId ORDER BY l.loginTime DESC")
    Optional<Login> findTopByAttendUserIdOrderByLoginTimeDesc(@Param("userId") Long userId);

    // 특정 기간동안의 로그인, 로그아웃 시간 기록 조회
    @Query("SELECT l FROM Login l WHERE l.attend.user.id = :userId AND l.loginTime BETWEEN :startDate AND :endDate")
    List<Login> findUserLoginsWithinPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
