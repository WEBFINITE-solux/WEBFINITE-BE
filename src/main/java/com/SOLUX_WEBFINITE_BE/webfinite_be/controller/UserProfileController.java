package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.UserProfileResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.UserProfileUpdateRequestDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserProfileRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.UserProfileService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController // @RestController = @Controller + @ResponseBody
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;

    // 프로필 조회
    @GetMapping("/profile/{userId}")
    public UserProfileResponseDto findById(@PathVariable Long userId) {
        return userProfileService.findById(userId);
    }

    // 프로필 사진 수정
    @PatchMapping("/profile/{profileId}/image")
    public Long update(@PathVariable Long profileId, @RequestBody UserProfileUpdateRequestDto requestDto) {

        return userProfileService.update(profileId, requestDto);
    }
}
