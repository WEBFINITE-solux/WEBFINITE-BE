package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizChoice;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuestionType;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizQuestion;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final String openAIModel; // 모델 이름을 저장할 변수
    private final QuizService quizService;

    // 생성자에서 모델 이름을 받아옵니다
    public OpenAIService(RestTemplate restTemplate, @Value("${openai.model}") String openAIModel, @Lazy QuizService quizService) {
        this.restTemplate = restTemplate;
        this.openAIModel = openAIModel; // 모델 이름 초기화
        this.quizService = quizService;
    }

    //0124수정
    public List<QuizQuestion> generateQuizQuestions(CourseFile courseFile, String detailedRequirements, QuestionType questionType, int totalQuestions) {
        // 파일에서 텍스트 추출
        String fileContent = quizService.generateFileContent(courseFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 프롬프트 생성 (totalQuestions 추가)
        String prompt = generatePrompt(questionType, totalQuestions);

        // PDF 텍스트와 요구사항 결합
        String combinedContent = fileContent + "\n\n" +
                "Detailed Requirements: " + detailedRequirements + "\n\n" +
                prompt;

        // OpenAI API 요청 및 응답 처리
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", openAIModel);
        requestBody.put("messages", new JSONArray(Arrays.asList(
                new JSONObject().put("role", "user").put("content", combinedContent)
        )));

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                new HttpEntity<>(requestBody.toString(), headers),
                String.class
        );

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String generatedContent = jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // 질문 파싱
        List<QuizQuestion> quizQuestions = new ArrayList<>();
        String[] questions = generatedContent.split("\n\n"); // 문제별로 구분
        for (String questionText : questions) {
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuestionContent(extractQuestion(questionText));
            quizQuestion.setQuestionType(determineQuestionType(questionText));
            quizQuestion.setAnswer(extractAnswer(questionText));
            quizQuestion.setExplanation(extractExplanation(questionText));

            // 객관식 및 참/거짓 문제의 경우 선택지 설정
            if (quizQuestion.getQuestionType() != QuestionType.SUBJECTIVE) {
                List<QuizChoice> choices = extractChoices(questionText);
                choices.forEach(quizQuestion::addQuizChoice);
            }

            quizQuestions.add(quizQuestion);
        }

        return quizQuestions;
    }





    private String extractQuestion(String questionText) {
        int startIdx = questionText.indexOf("Question: ") + "Question: ".length();
        int endIdx = questionText.indexOf("\n", startIdx);

        return questionText.substring(startIdx, endIdx != -1 ? endIdx : questionText.length()).trim();
    }



    // 선택지를 추출하는 메서드
    private List<QuizChoice> extractChoices(String questionText) {
        List<QuizChoice> choices = new ArrayList<>();
        String[] lines = questionText.split("\n");

        for (String line : lines) {
            if (line.matches("^[A-D]\\).*")) { // A), B), C), D)로 시작하는지 확인
                QuizChoice choice = new QuizChoice();
                choice.setChoiceContent(line.substring(2).trim()); // 'A)' 이후의 내용만 저장
                choices.add(choice);
            }
        }
        return choices;
    }



    private String extractAnswer(String questionText) {
        int startIdx = questionText.indexOf("Answer: ") + "Answer: ".length();
        int endIdx = questionText.indexOf("\n", startIdx);

        return questionText.substring(startIdx, endIdx != -1 ? endIdx : questionText.length()).trim();
    }



    private String extractExplanation(String questionText) {
        int startIdx = questionText.indexOf("Explanation: ") + "Explanation: ".length();
        return startIdx >= "Explanation: ".length() ? questionText.substring(startIdx).trim() : "";
    }




    // 질문 유형을 판단하는 메서드 (예시로 주관식/객관식 구분)
    private QuestionType determineQuestionType(String questionText) {
        if (questionText.contains("A)") || questionText.contains("B)") || questionText.contains("C)") || questionText.contains("D)")) {
            return QuestionType.MULTIPLE_CHOICE;
        } else if (questionText.contains("True") || questionText.contains("False")) {
            return QuestionType.TRUE_FALSE;
        } else {
            return QuestionType.SUBJECTIVE;  // 기본적으로 SUBJECTIVE로 처리
        }
    }




    //0124 수정
    private String generatePrompt(QuestionType questionType, int totalQuestions) {
        StringBuilder promptBuilder = new StringBuilder();
        for (int i = 1; i <= totalQuestions; i++) {
            switch (questionType) {
                case MULTIPLE_CHOICE:
                    promptBuilder.append("Please create a multiple-choice question with 4 options (A, B, C, D) and provide the correct answer and explanation. ")
                            .append("Format the output exactly like this:\n")
                            .append("Question: <question>\n")
                            .append("A) <choice A>\n")
                            .append("B) <choice B>\n")
                            .append("C) <choice C>\n")
                            .append("D) <choice D>\n")
                            .append("Answer: <correct answer letter>\n")
                            .append("Explanation: <explanation>\n\n");
                    break;

                case TRUE_FALSE:
                    promptBuilder.append("Please create a True/False question with the correct answer and explanation. ")
                            .append("Format the output exactly like this:\n")
                            .append("Question: <question>\n")
                            .append("True\n")
                            .append("False\n")
                            .append("Answer: <correct answer letter>\n")
                            .append("Explanation: <explanation>\n\n");
                    break;

                case SUBJECTIVE:
                    promptBuilder.append("Please create an open-ended question and provide the best possible answer as the explanation. ")
                            .append("Format the output exactly like this:\n")
                            .append("Question: <question>\n")
                            .append("Answer: <answer>\n")
                            .append("Explanation: <explanation>\n\n");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported question type: " + questionType);
            }
        }
        return promptBuilder.toString().trim();
    }



}