package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class FileContent extends Content{
    private FileUrl fileUrl;

    public FileContent(String type, FileUrl fileUrl){
        super(type);
        this.fileUrl = fileUrl;
    }
}
