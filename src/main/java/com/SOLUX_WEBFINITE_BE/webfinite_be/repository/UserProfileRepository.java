package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    public Optional<UserProfile> findByUserId(Long userId);
}
