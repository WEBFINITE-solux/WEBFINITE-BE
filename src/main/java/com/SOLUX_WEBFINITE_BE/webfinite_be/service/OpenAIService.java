package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizChoice;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuestionType;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizQuestion;
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

    private static final String OPENAI_URL = "https://api.openai.com/v1/completions";
    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<QuizQuestion> generateQuizQuestions(String fileContent, String detailedRequirements) {
        try {
            // OpenAI API 호출을 위한 요청 본문 작성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", "Create quiz questions based on the following content and detailed requirements:\n"
                    + detailedRequirements + "\nContent: " + fileContent);
            requestBody.put("max_tokens", 500);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 응답에서 생성된 퀴즈 질문 추출
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String generatedQuestions = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");

            // 문제와 선택지 생성
            List<QuizQuestion> quizQuestions = new ArrayList<>();
            String[] questions = generatedQuestions.split("\n");
            for (String questionText : questions) {
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuestionContent(questionText);

                if (questionText.contains("True/False")) {
                    quizQuestion.setQuestionType(QuestionType.TRUE_FALSE);
                    List<QuizChoice> choices = Arrays.asList(
                            new QuizChoice("True"),
                            new QuizChoice("False")
                    );
                    choices.forEach(quizQuestion::addQuizChoice);
                } else if (questionText.contains("open-ended")) {
                    quizQuestion.setQuestionType(QuestionType.SUBJECTIVE);
                } else {
                    quizQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                    List<QuizChoice> choices = Arrays.asList(
                            new QuizChoice("A"),
                            new QuizChoice("B"),
                            new QuizChoice("C"),
                            new QuizChoice("D")
                    );
                    choices.forEach(quizQuestion::addQuizChoice);
                }

                quizQuestions.add(quizQuestion);
            }

            // 생성된 퀴즈 질문을 반환
            return quizQuestions;

        } catch (Exception e) {
            throw new RuntimeException("Error generating quiz questions: " + e.getMessage());
        }
    }




    // 정답 생성 메서드 (이미 구현된 대로 사용)
    public String generateAnswerFromQuestion(String questionContent, QuestionType questionType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", generatePrompt(questionContent, questionType));
            requestBody.put("max_tokens", 150);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String generatedText = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");

            // 주관식 문제에 대한 정답 생성
            if (questionType == QuestionType.SUBJECTIVE) {
                return generatedText.trim(); // 주관식 문제는 OpenAI의 응답을 그대로 정답으로 설정
            }

            return generatedText.trim();

        } catch (Exception e) {
            throw new RuntimeException("Error generating answer from question: " + e.getMessage());
        }
    }

    private String generatePrompt(String questionContent, QuestionType questionType) {
        switch (questionType) {
            case MULTIPLE_CHOICE:
                return "Create a multiple choice question with 4 options (A, B, C, D) and provide the correct answer for the following question: " + questionContent;
            case TRUE_FALSE:
                return "Provide the correct answer (True or False) for the following statement: " + questionContent;
            case SUBJECTIVE:
                return "Provide the best possible answer for the following open-ended question: " + questionContent;
            default:
                return "Provide the correct answer for the following question: " + questionContent;
        }
    }
}
