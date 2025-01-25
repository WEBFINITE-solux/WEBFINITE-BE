package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Course;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.LearningPlan;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.PlanDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SimpleResponse;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt.GeneratePrompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.FileNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.CourseRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.FileRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.PlanRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.PromptRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanService {
    private final CourseRepository courseRepository;
    private final PlanRepository planRepository;
    private final FileRepository fileRepository;
    private final PromptRepository promptRepository;

    private final GPTService gptService;

    public PlanDTO getPlan(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        // 프롬프트 조회 (없을 경우 기본값 사용)
        String promptText = promptRepository.findByCourseId(courseId)
                .map(Prompt::getDescription)
                .orElse("학습 계획을 작성해주세요.");
        // 학습 계획 조회 (없을 경우 빈 리스트 반환)
        List<LearningPlan> plans = planRepository.findPlansByCourseId(courseId);

        return new PlanDTO(promptText, PlanDTO.toPlanDTO(plans));
    }

    public SimpleResponse createPlan(Long courseId, String promptText, LocalDate startDate, LocalDate endDate, String startUnit, String endUnit, Long fileId) throws IOException {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        CourseFile courseFile = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException());
        String filePath = courseFile.getFilePath();
        File file = new File(filePath);

        Prompt prompt = Prompt.createPrompt(startDate, endDate, startUnit, endUnit, promptText);
        String getText = "";

        // 파일이 pdf면 텍스트 추출, txt면 그대로 읽기
        if(file.getName().endsWith(".txt")){
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            getText = sb.toString();
        }
        else if(file.getName().endsWith(".pdf")){
            getText = pdfToText(file);
        }
        else {
            throw new IOException("지원하지 않는 파일 형식입니다.");
        }

        String requestPrompt = GeneratePrompt.createPlanPrompt(course.getTitle(), startDate.toString(), endDate.toString(), startUnit, endUnit, promptText, getText);

        String response = gptService.chat(requestPrompt);

        if(response.startsWith("```")){
            response = response.replaceAll("(?i)```JSON\\s*```", ""); // 혹시나 마크다운 코드블럭 표시가 있을 경우 제거
        }

        List<LearningPlan> plans = textToPlan(response);

        // 이미 prompt가 존재할 경우 수정, 없을 경우 생성
        if(promptRepository.findByCourseId(courseId).isPresent()){
            Prompt exist = promptRepository.findByCourseId(courseId).get();
            exist.setStartDate(prompt.getStartDate());
            exist.setEndDate(prompt.getEndDate());
            exist.setStartUnit(prompt.getStartUnit());
            exist.setEndUnit(prompt.getEndUnit());
            exist.setDescription(prompt.getDescription());

            planRepository.deletePlanByCourseId(courseId);
        }
        else {
            prompt.setCourse(course); // 얘땜에 오류 났었음..
            promptRepository.save(prompt);
        }
        for(LearningPlan plan : plans){
            plan.setCourse(course);
            planRepository.save(plan);
        }

        return new SimpleResponse("학습 계획 생성 완료");
    }

    public SimpleResponse updatePlan(Long courseId, PlanDTO plans) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException());
        List<LearningPlan> learningPlans = planRepository.findPlansByCourseId(courseId);
        if(learningPlans.isEmpty()){
            throw new EmptyPlanListException();
        }

        // 학습 계획의 ID를 Set으로 추출
        Set<Long> existingPlanIds = learningPlans.stream()
                .map(LearningPlan::getId)
                .collect(Collectors.toSet());

        // 요청 데이터의 plan_id 중 데이터베이스에 없는 ID를 필터링
        List<Long> invalidPlanIds = plans.getLearningPlan().stream()
                .map(PlanDTO.Plan::getPlanId)
                .filter(planId -> !existingPlanIds.contains(planId))
                .collect(Collectors.toList());

        // 데이터베이스에 없는 plan_id가 있는 경우 예외 발생
        if (!invalidPlanIds.isEmpty()) {
            throw new PlanNotFoundException();
        }

        // plan_id 기준으로 plans와 learningPlans를 비교하여 수정할 것 수정
        for(LearningPlan plan : learningPlans){
            for(PlanDTO.Plan planDTO : plans.getLearningPlan()){
                if(plan.getId().equals(planDTO.getPlanId())){
                    plan.setTitle(plan.getTitle());
                    plan.setDescription(planDTO.getPlanDescription());
                }
            }
        }
        return new SimpleResponse("학습 계획 수정 완료");
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

    private List<LearningPlan> textToPlan (String text) throws JsonMappingException {
        List<LearningPlan> plans = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            plans = objectMapper.readValue(text, new TypeReference<List<LearningPlan>>() {});
            return plans;
        } catch (JsonMappingException e) {
            throw new JsonMappingException("JSON 변환 중 오류가 발생했습니다. 다시 시도해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
