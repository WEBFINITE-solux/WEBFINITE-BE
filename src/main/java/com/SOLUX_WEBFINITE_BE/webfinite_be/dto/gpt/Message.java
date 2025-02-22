package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String role;
    private String content;
}
