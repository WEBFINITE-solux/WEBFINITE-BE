package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.request;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileMessage extends Message {
    private List<Content> content;

    public FileMessage(String role, List<Content> content){
        super(role);
        this.content = content;
    }

}
