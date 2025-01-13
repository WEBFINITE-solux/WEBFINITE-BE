package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class TextContent extends Content {
    private String text;

    public TextContent(String type, String text){
        super(type);
        this.text = text;
    }
}
