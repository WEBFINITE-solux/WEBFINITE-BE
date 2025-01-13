package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextMessage extends Message {
    private String content;

    public TextMessage(String role, String content){
        super(role);
        this.content = content;
    }
}
