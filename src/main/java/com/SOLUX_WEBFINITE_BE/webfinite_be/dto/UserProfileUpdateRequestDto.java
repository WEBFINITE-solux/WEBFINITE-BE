package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Icon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileUpdateRequestDto {

    @Schema
    private Icon icon;

    @Builder
    public UserProfileUpdateRequestDto(Icon icon) {
        this.icon = icon;
    }
}
