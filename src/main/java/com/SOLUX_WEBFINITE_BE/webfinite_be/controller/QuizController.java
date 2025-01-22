package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // 1. 생성된 퀴즈 목록 조회(user_id와 course_id로 필터링)
    @GetMapping("/{user_id}/course/{courseId}")
    public ResponseEntity<List<QuizListResponseDto>> getQuizzesByUserAndCourse(
            @PathVariable("user_id") Long userId,
            @PathVariable("courseId") Long courseId) {

        List<QuizListResponseDto> quizzes = quizService.getQuizzesByUserAndCourse(userId, courseId);
        return ResponseEntity.ok(quizzes);
    }

    // 2. 퀴즈 생성
    @PostMapping("/create/{courseId}/{fileId}")
    public ResponseEntity<QuizDetailResponseDto> createQuiz(
            @PathVariable Long courseId,          // courseId는 URL 경로에서 받음
            @PathVariable Long fileId,           // fileId로 수정
            @RequestBody QuizRequestDto quizRequestDto) {  // 나머지 정보는 요청 본문에서 받음

        // fileId를 사용하여 퀴즈 생성
        QuizDetailResponseDto quizDetail = quizService.createQuizFromFile(courseId, fileId, quizRequestDto);
        return ResponseEntity.ok(quizDetail);
    }

    // 3. 퀴즈 조회
    @GetMapping("/{quiz_id}")
    public ResponseEntity<QuizDetailResponseDto> getQuizDetails(@PathVariable("quiz_id") Long quizId) {
        QuizDetailResponseDto quizDetails = quizService.getQuizDetails(quizId);
        return ResponseEntity.ok(quizDetails);
    }

    // 4. 답안 제출
    @PostMapping("/submit")
    public ResponseEntity<SubmitQuizResponseDto> submitAnswers(
            @RequestBody SubmitQuizRequestDto submissionRequestDto) {
        // SubmitQuizRequestDto에서 quizId를 자동으로 가져오므로 PathVariable은 필요하지 않음
        SubmitQuizResponseDto response = quizService.submitAnswers(submissionRequestDto.getQuizId(), submissionRequestDto);
        return ResponseEntity.ok(response);
    }

    // 5. 수정된 답안 제출
    @PatchMapping("/submit")
    public ResponseEntity<SubmitQuizResponseDto> updateAnswers(
            @RequestBody SubmitQuizRequestDto submissionRequestDto) {
        // 수정된 답안 리스트를 포함한 응답 반환
        SubmitQuizResponseDto response = quizService.updateAnswers(submissionRequestDto);
        return ResponseEntity.ok(response);
    }



    // 6. 채점 결과 조회
    @GetMapping("/{quiz_id}/result")
    public ResponseEntity<QuizResultResponseDto> getQuizResult(@PathVariable("quiz_id") Long quizId) {
        QuizResultResponseDto result = quizService.getQuizResult(quizId);
        return ResponseEntity.ok(result);
    }

    // 7. 퀴즈 삭제
    @DeleteMapping("/{quiz_id}")
    public ResponseEntity<Map<String, String>> deleteQuiz(@PathVariable("quiz_id") Long quizId) {
        quizService.deleteQuiz(quizId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "퀴즈와 관련된 질문, 선택지, 사용자 답안이 모두 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
