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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final String openAIModel; // 모델 이름을 저장할 변수
    private final QuizService quizService;

    public OpenAIService(RestTemplate restTemplate, @Value("${openai.model}") String openAIModel, @Lazy QuizService quizService) {
        this.restTemplate = restTemplate;
        this.openAIModel = openAIModel;
        this.quizService = quizService;
    }

    public List<QuizQuestion> generateQuizQuestions(CourseFile courseFile, String detailedRequirements, QuestionType questionType, int totalQuestions) {
        String fileContent = quizService.generateFileContent(courseFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = generatePrompt(questionType, totalQuestions);

        String combinedContent = fileContent + "\n\n" + "Detailed Requirements: " + detailedRequirements + "\n\n" + prompt;

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

        String responseBody = response.getBody();

        //테스트 해 볼 부분
        // OpenAI 응답 본문 출력
        System.out.println("OpenAI 응답 본문: " + responseBody);  // 여기서 응답 본문 출력

        if (responseBody == null || responseBody.isBlank()) {
            throw new RuntimeException("OpenAI API 응답이 비어 있습니다.");
        }

        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            String generatedContent = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            //json 형식 응답 변동성 고려
            if (generatedContent.startsWith("```json")) {
                generatedContent = generatedContent.replaceAll("```json", "").replaceAll("```", "").trim();
            }


            JSONObject resultJson = new JSONObject(generatedContent);
            JSONArray questionsArray = resultJson.getJSONArray("questions");
            List<QuizQuestion> quizQuestions = new ArrayList<>();

            int questionsToAdd = Math.min(questionsArray.length(), totalQuestions);

            for (int i = 0; i < questionsToAdd; i++) {
                JSONObject qObj = questionsArray.getJSONObject(i);
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuestionContent(qObj.getString("questionContent"));

                if (questionType == QuestionType.MULTIPLE_CHOICE) {
                    if (qObj.has("choices")) {
                        JSONArray choicesArray = qObj.getJSONArray("choices");
                        List<QuizChoice> choices = new ArrayList<>();
                        for (int j = 0; j < choicesArray.length(); j++) {
                            JSONObject choiceObj = choicesArray.getJSONObject(j);
                            QuizChoice choice = new QuizChoice();
                            choice.setChoiceContent(choiceObj.getString("choiceContent"));
                            choice.setQuizQuestion(quizQuestion);
                            choices.add(choice);
                        }
                        quizQuestion.setQuizChoices(choices);
                    }
                } else {
                    quizQuestion.setQuizChoices(new ArrayList<>()); // 선택지 없음
                }

                quizQuestion.setAnswer(qObj.getString("answer"));
                quizQuestion.setExplanation(qObj.getString("explanation"));
                quizQuestion.setQuestionType(questionType);
                quizQuestions.add(quizQuestion);
            }

            return quizQuestions;
        } catch (Exception e) {
            System.err.println("응답 본문 JSON 파싱 실패: " + e.getMessage());
            throw new RuntimeException("응답 본문 파싱 오류", e);
        }
    }

    //다시 고쳐본 부분
    private String generatePrompt(QuestionType questionType, int totalQuestions) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("다음 조건을 만족하는 문제를 한국어로 만들어 주세요. 출력은 반드시 아래 JSON 형식만 사용하고, 추가 텍스트(예: 설명, 백틱 등)는 포함하지 말아 주세요. "
                + "questionType에 맞게 문제를 생성할 때 모든 문제는 동일한 questionType이어야 합니다.\n");

        switch (questionType) {
            case MULTIPLE_CHOICE:
                promptBuilder.append("questionType이 MULTIPLE_CHOICE인 경우 선택지를 포함한 문제를 작성해주세요.\n");
                break;
            case TRUE_FALSE:
                promptBuilder.append("questionType이 TRUE_FALSE인 경우 문제는 참/거짓만 포함하고, 선택지는 비워두세요.\n");
                break;
            case SUBJECTIVE:
                promptBuilder.append("questionType이 SUBJECTIVE인 경우 문제는 자유롭게 답을 작성하는 문제여야 하며, 선택지는 비워두세요.\n");
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 문제 유형: " + questionType);
        }

        promptBuilder.append("{\n")
                .append("  \"quizId\": <퀴즈ID>,\n")
                .append("  \"quizTitle\": \"Quiz for [과목명] - [파일명].pdf\",\n")
                .append("  \"courseName\": \"[과목명]\",\n")
                .append("  \"quizType\": \"\",\n")
                .append("  \"questions\": [\n");

        for (int i = 1; i <= totalQuestions; i++) {
            promptBuilder.append("    {\n")
                    .append("      \"questionId\": <문제ID>,\n")
                    .append("      \"questionContent\": \"<문제 내용>\",\n");

            switch (questionType) {
                case MULTIPLE_CHOICE:
                    promptBuilder.append("      \"choices\": [\n")
                            .append("         { \"choiceId\": 1, \"choiceContent\": \"<선택지 A>\" },\n")
                            .append("         { \"choiceId\": 2, \"choiceContent\": \"<선택지 B>\" },\n")
                            .append("         { \"choiceId\": 3, \"choiceContent\": \"<선택지 C>\" },\n")
                            .append("         { \"choiceId\": 4, \"choiceContent\": \"<선택지 D>\" }\n")
                            .append("      ],\n")
                            .append("      \"answer\": \"<선택지 내용으로 정답을 입력하세요. 예: '이름 공간을 관리하기 위해'>\",\n"); // 선택지 내용으로 정답 입력
                    break;
                case TRUE_FALSE:
                    promptBuilder.append("      \"choices\": [],\n")
                            .append("      \"answer\": \"답변은 참/거짓으로 작성해주세요. 예: '참' 또는 '거짓'으로 작성해주세요.\",\n"); // TRUE_FALSE에서는 참/거짓만
                    break;
                case SUBJECTIVE:
                    promptBuilder.append("      \"choices\": [],\n")
                            .append("      \"answer\": \"이름 공간을 사용하면 이름 충돌을 방지할 수 있습니다.\",\n"); //
                    break;
                default:
                    throw new IllegalArgumentException("지원되지 않는 문제 유형: " + questionType);
            }

            promptBuilder.append("      \"explanation\": \"<해설>\",\n")
                    .append("      \"questionType\": \"<문제 유형>\"\n")
                    .append("    },\n");
        }

        promptBuilder.deleteCharAt(promptBuilder.length() - 2); // 마지막 쉼표 제거
        promptBuilder.append("  ]\n")
                .append("}\n\n");

        return promptBuilder.toString().trim();
    }

}
