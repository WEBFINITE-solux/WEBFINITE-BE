package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.request;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGPTRequest {
    @JsonProperty("model")
    private String model;
    @JsonProperty("messages")
    private List<Message> messages;
    @JsonProperty("max_tokens")
    private int maxTokens;

    public static ChatGPTRequest createFileRequest(String model, int maxTokens, String role, String requestText, String fileUrl) {
        TextContent textContent = new TextContent("text", requestText);
        FileContent fileContent = new FileContent("file_url", new FileUrl(fileUrl));
        Message message = new FileMessage(role, List.of(textContent, fileContent));
        return createChatGPTRequest(model, maxTokens, Collections.singletonList(message));
    }

    public static ChatGPTRequest createTextRequest(String model, int maxTokens, String role, String requestText) {
        Message message = new TextMessage(role, requestText);
        return createChatGPTRequest(model, maxTokens, Collections.singletonList(message));
    }

    private static ChatGPTRequest createChatGPTRequest(String model, int maxTokens, List<Message> messages) {
        return ChatGPTRequest.builder()
                .model(model)
                .maxTokens(maxTokens)
                .messages(messages)
                .build();
    }
}
