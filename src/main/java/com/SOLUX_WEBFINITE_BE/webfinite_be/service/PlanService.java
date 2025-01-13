package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.GeneratePrompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.response.ChatGPTResponse;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.CourseNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.PlanRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;



import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final CourseRepository courseRepository;
    private final PlanRepository planRepository;

    private final GPTService gptService;

    public String createPlan(Long courseId, String promptText, LocalDate startDate, LocalDate endDate, String startUnit, String endUnit, Long fileId) throws IOException {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        CourseFile courseFile = courseRepository.findFileById(fileId).orElseThrow(() -> new IllegalStateException("파일이 존재하지 않습니다."));
        String filePath = courseFile.getFilePath();
        File file = new File(filePath);

        Prompt prompt = Prompt.createPrompt(startDate, endDate, startUnit, endUnit, promptText);
        prompt.setCourse(course);

        String requestPrompt = GeneratePrompt.createLearningPlanPrompt(course.getTitle(), startDate.toString(), endDate.toString(), startUnit, endUnit, promptText);

        String reponseFile = gptService.uploadFile(convertFileToMultipartFile(file));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(reponseFile);
        String savedFileId = rootNode.get("id").asText();

        /*ChatGPTResponse*/ String response = gptService.requestFile(/*(MultipartFile) convertFileToMultipartFile(file)*/savedFileId, requestPrompt);

        gptService.deleteFile(savedFileId);

        //String result = response.getChoices().get(0).getMessage().getContent();

        planRepository.savePrompt(prompt);

        return /*result*/response;
    }

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IllegalStateException("파일이 존재하지 않습니다: " + file.getAbsolutePath());
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return new MockMultipartFile(
                    file.getName(),                      // 파일 이름
                    file.getName(),                      // 원본 파일 이름
                    Files.probeContentType(file.toPath()), // 파일의 Content-Type
                    inputStream                          // 파일 데이터
            );
        }
    }
}
