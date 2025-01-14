package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.LearningPlan;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.PlanDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.GeneratePrompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.CourseNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.PlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    private final CourseRepository courseRepository;
    private final PlanRepository planRepository;

    private final GPTService gptService;

    public PlanDTO getPlan(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        String promptText = planRepository.findPromptByCourseId(courseId).orElseThrow(() -> new IllegalStateException("프롬프트가 존재하지 않습니다.")).getDescription();
        List<LearningPlan> plans = planRepository.findPlansByCourseId(courseId);

        if(plans.isEmpty()){
            throw new IllegalStateException("학습 계획이 존재하지 않습니다.");
        }

        return new PlanDTO(promptText, PlanDTO.toPlanDTO(plans));
    }

    public Map<String, String> createPlan(Long courseId, String promptText, LocalDate startDate, LocalDate endDate, String startUnit, String endUnit, Long fileId) throws IOException {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        CourseFile courseFile = courseRepository.findFileById(fileId).orElseThrow(() -> new IllegalStateException("파일이 존재하지 않습니다."));
        String filePath = courseFile.getFilePath();
        File file = new File(filePath);

        Prompt prompt = Prompt.createPrompt(startDate, endDate, startUnit, endUnit, promptText);
        prompt.setCourse(course);

        String getText = pdfToText(file);

        String requestPrompt = GeneratePrompt.createPlanPrompt(course.getTitle(), startDate.toString(), endDate.toString(), startUnit, endUnit, promptText, getText);

        String response = gptService.chat(requestPrompt);

        List<LearningPlan> plans = textToPlan(response);

        for(LearningPlan plan : plans){
            plan.setCourse(course);
            planRepository.savePlan(plan);
        }

        planRepository.savePrompt(prompt);

        return Map.of("message", "학습 계획 생성 완료");
    }

    private String pdfToText(File file) throws IOException {
        try {
            PDDocument pdfDoc = PDDocument.load(file);
            String text = new PDFTextStripper().getText(pdfDoc);
            return text;
        }catch (IOException e){
            throw new IOException("파일을 읽을 수 없습니다.");
        }
    }

    private List<LearningPlan> textToPlan (String text){
        List<LearningPlan> plans = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            plans = objectMapper.readValue(text, new TypeReference<List<LearningPlan>>() {});
            return plans;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
