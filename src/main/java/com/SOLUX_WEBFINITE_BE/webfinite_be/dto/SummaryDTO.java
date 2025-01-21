package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryDTO {
    @JsonProperty("summary_id")
    private Long summaryId;
    @JsonProperty("summary_content")
    private String content;
}
