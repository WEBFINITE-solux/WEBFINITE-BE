package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDTO {
    @JsonProperty("file_id")
    private Long fileId;
    @JsonProperty("original_filename")
    private String originalFilename;
    @JsonProperty("is_summarized")
    private boolean isSummarized;
}