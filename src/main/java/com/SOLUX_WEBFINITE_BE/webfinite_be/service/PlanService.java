package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.GeneratePrompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.CourseNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.reposiroty.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;

@Service
@Transactional
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

        String getText = pdfToText(file);

        String requestPrompt = GeneratePrompt.createPlanPrompt(course.getTitle(), startDate.toString(), endDate.toString(), startUnit, endUnit, promptText, getText);

        planRepository.savePrompt(prompt);

        return gptService.chat(requestPrompt);
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
}
