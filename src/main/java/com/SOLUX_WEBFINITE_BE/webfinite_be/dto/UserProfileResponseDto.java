package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Icon;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserProfile;
import lombok.Getter;

@Getter
public class UserProfileResponseDto {

    private Long profileId;
    private Long userId;
    private Icon icon;
    private String introduction;

    public UserProfileResponseDto(UserProfile entity) {
        this.profileId = entity.getId(); // UserProfile의 프로필 아이디
        this.userId = entity.getUser().getId(); // User의 아이디
        this.icon = entity.getIcon();
        this.introduction = entity.getIntroduction();
    }
}
