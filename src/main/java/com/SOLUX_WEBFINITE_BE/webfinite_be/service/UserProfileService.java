package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserProfile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.UserProfileResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.UserProfileUpdateRequestDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserProfileRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    // 프로필 조회
    @Transactional
    public UserProfileResponseDto findById(Long userId) {
        UserProfile entity = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 프로필 정보가 없습니다. profileId = " + userId)); // 예외처리 필요

        return new UserProfileResponseDto(entity);
    }

    // 프로필 사진 수정
    @Transactional
    public Long update(Long profileId, UserProfileUpdateRequestDto requestDto) {

        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로필이 없습니다. profileId = " + profileId)); // 추후 예외처리

        userProfile.setIcon(requestDto.getIcon());

        return profileId;
    }

}
