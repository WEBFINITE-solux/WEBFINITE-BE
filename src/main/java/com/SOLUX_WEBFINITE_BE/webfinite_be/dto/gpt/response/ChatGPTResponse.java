package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponse {
    @JsonProperty("choices")
    private List<Choice> choices;
}
