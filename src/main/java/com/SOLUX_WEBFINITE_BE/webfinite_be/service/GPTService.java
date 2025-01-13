package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.ChatGPTRequest;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class GPTService {
    @Value("${openai.model}")
    private String apiModel;

    @Value("${openai.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public String chat(String prompt){
        ChatGPTRequest request = new ChatGPTRequest(apiModel, prompt);
        ChatGPTResponse response = restTemplate.postForObject(apiUrl, request, ChatGPTResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
