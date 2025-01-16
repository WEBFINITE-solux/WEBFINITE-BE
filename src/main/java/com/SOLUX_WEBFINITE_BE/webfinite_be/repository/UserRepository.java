package com.SOLUX_WEBFINITE_BE.webfinite_be.repository;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByLoginUserId(String loginUserId);

    Optional<User> findByEmail(String email);
}
