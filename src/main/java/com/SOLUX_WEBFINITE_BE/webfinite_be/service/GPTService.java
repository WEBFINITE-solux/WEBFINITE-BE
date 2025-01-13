package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.request.ChatGPTRequest;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.response.ChatGPTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPTService {
    @Value("${openai.model}")
    private String apiModel;

    @Value("${openai.url}")
    private String apiUrl;

    @Value("${openai.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ChatGPTResponse requestText(String requestText){
        ChatGPTRequest request = ChatGPTRequest.createTextRequest(apiModel, 500, "user", requestText);
        return restTemplate.postForObject(apiUrl, request, ChatGPTResponse.class);
    }

    public /*ChatGPTResponse*/ String requestFile(/*MultipartFile file*/ String fileContent, String requestText) throws IOException {
        /*String base64File = Base64.getEncoder().encodeToString(file.getBytes());
        String fileUrl = "data:application/pdf;base64," + base64File;
        ChatGPTRequest request = ChatGPTRequest.createFileRequest(apiModel, 500, "user", requestText, fileUrl);
        return restTemplate.postForObject(apiUrl, request, ChatGPTResponse.class);*/
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 요청 데이터
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", requestText + "\nFile Content:\n" + fileContent)
        ));
        requestBody.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        String apiUrl = "https://api.openai.com/v1/chat/completions";
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        return response.getBody();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(apiKey);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename(){
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("purpose", "assistants");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String apiUrl = "https://api.openai.com/v1/files";
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        return response.getBody(); // 업로드 응답 (file_id 포함)
    }

    public void deleteFile(String fileId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);

        // 요청 생성
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // DELETE 요청 보내기
        String deleteUrl = "https://api.openai.com/v1/files/" + fileId;
        ResponseEntity<String> response = restTemplate.exchange(deleteUrl, org.springframework.http.HttpMethod.DELETE, requestEntity, String.class);

        // 응답 출력
        System.out.println("Response: " + response.getBody());
    }
}
