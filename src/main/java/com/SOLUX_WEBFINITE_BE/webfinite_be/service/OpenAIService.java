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

        // 문제의 수가 totalQuestions를 넘지 않도록 제한
        int questionsToAdd = Math.min(questions.length, totalQuestions);

        for (int i = 0; i < questionsToAdd; i++) {
            String questionText = questions[i];

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





    // 질문을 추출하는 메서드
    private String extractQuestion(String questionText) {
        // 질문이 "문제:"로 시작한다고 가정하고, 그 뒤에 실제 질문 내용이 온다고 가정
        int startIdx = questionText.indexOf("문제: ") + "문제: ".length();
        int endIdx = questionText.indexOf("\n", startIdx);
        if (startIdx != -1 && endIdx != -1) {
            return questionText.substring(startIdx, endIdx).trim();
        }
        // 질문 끝이 없으면 전체 텍스트를 질문으로 취급
        return questionText.substring(startIdx).trim();
    }

    // 선택지를 추출하는 메서드
    private List<QuizChoice> extractChoices(String questionText) {
        List<QuizChoice> choices = new ArrayList<>();
        // 문제 텍스트에서 선택지 부분만 추출
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

    // 정답을 추출하는 메서드
    private String extractAnswer(String questionText) {
        // 정답 부분을 추출
        int startIdx = questionText.indexOf("정답: ") + "정답: ".length();
        int endIdx = questionText.indexOf("\n", startIdx);
        if (startIdx != -1 && endIdx != -1) {
            return questionText.substring(startIdx, endIdx).trim();
        }
        // 정답이 없으면 빈 문자열 반환
        return "";
    }

    // 해설을 추출하는 메서드
    private String extractExplanation(String questionText) {
        int startIdx = questionText.indexOf("해설: ") + "해설: ".length();
        if (startIdx >= "해설: ".length()) {
            return questionText.substring(startIdx).trim();
        }
        return ""; // 해설이 없으면 빈 문자열 반환
    }

    // 문제 유형을 판단하는 메서드
    private QuestionType determineQuestionType(String questionText) {
        if (questionText.contains("A)") || questionText.contains("B)") || questionText.contains("C)") || questionText.contains("D)")) {
            return QuestionType.MULTIPLE_CHOICE;
        } else if (questionText.contains("참") || questionText.contains("거짓")) {
            return QuestionType.TRUE_FALSE;
        } else {
            return QuestionType.SUBJECTIVE;
        }
    }






    //0124 수정
    private String generatePrompt(QuestionType questionType, int totalQuestions) {
        StringBuilder promptBuilder = new StringBuilder();
        for (int i = 1; i <= totalQuestions; i++) {
            switch (questionType) {
                case MULTIPLE_CHOICE:
                    promptBuilder.append("한국어로 4개의 선택지를 가진 객관식 문제를 만들어 주세요. 정답과 해설도 포함해 주세요. ")
                            .append("출력 형식은 다음과 같이 해주세요:\n")
                            .append("문제: <문제 내용>\n")
                            .append("A) <선택지 A>\n")
                            .append("B) <선택지 B>\n")
                            .append("C) <선택지 C>\n")
                            .append("D) <선택지 D>\n")
                            .append("정답: <정답 선택지 글자 및 내용>\n")
                            .append("해설: <해설>\n\n");
                    break;

                case TRUE_FALSE:
                    promptBuilder.append("한국어로 참/거짓 문제를 만들어 주세요. 정답과 해설도 포함해 주세요. ")
                            .append("출력 형식은 다음과 같이 해주세요:\n")
                            .append("문제: <문제 내용>\n")
                            .append("참\n")
                            .append("거짓\n")
                            .append("정답: <정답 선택지 글자>\n")
                            .append("해설: <해설>\n\n");
                    break;

                case SUBJECTIVE:
                    promptBuilder.append("한국어로 주관식 문제를 만들어 주세요. 가능한 최적의 답안을 해설로 제공해 주세요. ")
                            .append("출력 형식은 다음과 같이 해주세요:\n")
                            .append("문제: <문제 내용>\n")
                            .append("정답: <정답>\n")
                            .append("해설: <해설>\n\n");
                    break;

                default:
                    throw new IllegalArgumentException("지원되지 않는 문제 유형: " + questionType);
            }
        }
        return promptBuilder.toString().trim();
    }



}