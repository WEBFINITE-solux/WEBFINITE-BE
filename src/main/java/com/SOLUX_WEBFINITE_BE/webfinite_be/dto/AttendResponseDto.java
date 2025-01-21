package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Attend;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AttendResponseDto {

    private Long attendId;
    private Long userId;
    private LocalDate attendDate;
    private boolean isAttended;
    private int attendDateCnt;

    public AttendResponseDto(Attend entity) {
        this.attendId = entity.getId(); // Attend의 아이디
        this.userId = entity.getUser().getId(); // User의 아이디
        this.attendDate = entity.getAttendDate();
        this.isAttended = entity.isAttended();
        this.attendDateCnt = entity.getAttendDateCnt();
    }
}
