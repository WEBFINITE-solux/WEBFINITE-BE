package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Attend;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttendRepository extends JpaRepository<Attend, Long> {

    @Query("SELECT a FROM Attend a WHERE a.user.id = :userId")
    Optional<Attend> findByUserId(@Param("userId") Long userId);
}
