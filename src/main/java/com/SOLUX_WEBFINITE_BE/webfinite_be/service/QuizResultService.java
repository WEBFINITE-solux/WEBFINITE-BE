package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizChoice;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuestionType;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Quiz;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.QuizQuestion;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.UserAnswer;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.DetailedResultDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.QuizResultResponseDto;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.EmptyUserAnswerException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.QuizNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.QuizChoiceRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.QuizQuestionRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.QuizRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.UserAnswerRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizResultService {

    private final QuizRepository quizRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final OpenAIService openAIService;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizChoiceRepository quizChoiceRepository;

    @Autowired
    public QuizResultService(QuizRepository quizRepository, UserAnswerRepository userAnswerRepository, OpenAIService openAIService, QuizQuestionRepository quizQuestionRepository, QuizChoiceRepository quizChoiceRepository) {
        this.quizRepository = quizRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.openAIService = openAIService;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizChoiceRepository = quizChoiceRepository;
    }

    public QuizResultResponseDto getQuizResult(Long quizId) {
        // 퀴즈를 ID로 찾아옵니다.
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException());

        // 해당 퀴즈에 대한 사용자 답변을 가져옵니다.
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuiz_QuizId(quizId);

        if (userAnswers.isEmpty()) {
            throw new EmptyUserAnswerException();
        }

        // 주관식 문제의 정답 여부를 유사도 기준으로 계산합니다.
        int correctCount = (int) userAnswers.stream()
                .filter(answer -> {
                    QuizQuestion question = answer.getQuizQuestion();
                    if (question.getQuestionType() == QuestionType.SUBJECTIVE) {
                        return isSubjectiveAnswerCorrect(answer.getUserAnswer(), question.getAnswer());
                    }
                    return answer.getIsCorrect();  // 객관식 문제는 기존의 정답 여부 사용
                })
                .count();

        int totalQuestions = userAnswers.size();

        // 정답 비율을 계산합니다.
        String correctRate = totalQuestions > 0 ? correctCount + " / " + totalQuestions : "? / " + totalQuestions;

        // 상세 결과를 생성합니다.
        List<DetailedResultDto> detailedResults = generateDetailedResults(userAnswers);

        // 결과를 담아 QuizResultResponseDto를 반환합니다.
        return new QuizResultResponseDto(
                "퀴즈 채점 결과가 성공적으로 검색되었습니다.",  // 메시지
                quiz.getQuizTitle(),  // 퀴즈 제목
                quiz.getCourseName(),  // 강의명 추가 (퀴즈와 연관된 강의명)
                correctCount,  // 정답 개수
                totalQuestions,  // 총 문제 개수
                correctRate,  // 정답 비율
                detailedResults  // 상세 결과
        );
    }



    // calculateCorrectCount 메서드 추가
    public Long calculateCorrectCount(Quiz quiz) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuiz_QuizId(quiz.getQuizId());

        return userAnswers.stream()
                .filter(UserAnswer::getIsCorrect)
                .count();
    }

    private List<DetailedResultDto> generateDetailedResults(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .map(answer -> {
                    QuizQuestion question = answer.getQuizQuestion();

                    // QuizChoiceRepository를 통해 선택지 가져오기
                    List<String> choices = quizChoiceRepository.findByQuizQuestion_QuestionId(question.getQuestionId())
                            .stream()
                            .map(QuizChoice::getChoiceContent)
                            .collect(Collectors.toList());

                    List<String> choicesToUse = question.getQuestionType() == QuestionType.SUBJECTIVE ?
                            Collections.emptyList() : choices;

                    // 주관식 문제의 경우 정답 여부를 따로 확인
                    boolean isCorrect;

                    if (question.getQuestionType() == QuestionType.SUBJECTIVE) {
                        isCorrect = isSubjectiveAnswerCorrect(answer.getUserAnswer(), question.getAnswer());
                    } else {
                        isCorrect = answer.getIsCorrect(); // 객관식 문제는 기존의 정답 여부 사용
                    }

                    return new DetailedResultDto(
                            question.getQuestionId(),
                            question.getQuestionContent(),
                            choicesToUse,
                            answer.getUserAnswer(), // 항상 사용자 답변을 반환,
                            question.getAnswer(),
                            question.getExplanation(),
                            isCorrect
                    );
                })
                .collect(Collectors.toList());
    }




    private boolean isSubjectiveAnswerCorrect(String userAnswer, String correctAnswer) {
        // 입력 값이 null이거나 비어 있는 경우 false 반환
        if (userAnswer == null || correctAnswer == null) {
            return false;
        }

        // 한글 자모 분리 및 정규화
        String normalizedUserAnswer = normalizeHangul(userAnswer.trim());
        String normalizedCorrectAnswer = normalizeHangul(correctAnswer.trim());

        // LevenshteinDistance 계산
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(normalizedUserAnswer, normalizedCorrectAnswer);

        int maxLength = Math.max(normalizedUserAnswer.length(), normalizedCorrectAnswer.length());
        double similarity = 1.0 - (double) distance / maxLength;

        // 유사도가 0.8 이상이면 정답으로 간주
        return similarity >= 0.5;
    }

    private String normalizeHangul(String text) {
        // 한글을 자모 단위로 분리
        return Normalizer.normalize(text, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", ""); // 결합 문자를 제거
    }


    private String generateAnswer(QuizQuestion question) {
        // 주관식 문제의 경우 정답을 그대로 반환
        if (question.getQuestionType() == QuestionType.SUBJECTIVE) {
            return question.getAnswer();
        }

        // 다른 유형의 문제의 경우에도 정답 반환
        return question.getAnswer();
    }

}
