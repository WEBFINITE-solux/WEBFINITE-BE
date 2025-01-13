package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.response;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.TextMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
    private int index;
    private TextMessage message;
}
