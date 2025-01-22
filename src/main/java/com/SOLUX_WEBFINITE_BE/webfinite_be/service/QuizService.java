package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.QuizNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.QuizQuestionNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.UserNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class QuizService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final CourseRepository courseRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final OpenAIService openAIService;
    private final QuizResultService quizResultService;
    private final QuizChoiceRepository quizChoiceRepository;

    // 1. 생성된 퀴즈 목록 조회
    public List<QuizListResponseDto> getQuizzesByUserAndCourse(Long userId, Long courseId) {
        List<Quiz> quizzes = quizRepository.findQuizzesByUserIdAndCourseId(userId, courseId);

        return quizzes.stream().map(quiz -> {
            Long correctCount = quizResultService.calculateCorrectCount(quiz);
            Long totalQuestions = (long) quiz.getQuizQuestions().size();

            String correctRate;
            if (quiz.getQuizState() == QuizState.IN_PROGRESS) {
                correctRate = "? / " + totalQuestions;
            } else if (quiz.getQuizState() == QuizState.COMPLETED) {
                correctRate = correctCount + " / " + totalQuestions;
            } else {
                throw new IllegalStateException("Unexpected QuizState: " + quiz.getQuizState());
            }

            // 강의 제목을 추가
            String courseTitle = quiz.getCourse().getTitle(); // 강의 이름 가져오기

            return new QuizListResponseDto(
                    quiz.getQuizId(),
                    quiz.getQuizTitle(),
                    courseTitle,  // 강의 이름 포함
                    quiz.getQuizState(),
                    correctRate
            );
        }).collect(Collectors.toList());
    }

    // 2. 강의 자료를 기반으로 퀴즈 생성
    @Transactional
    public QuizDetailResponseDto createQuizFromFile(Long courseId, Long fileId, QuizRequestDto quizRequestDto) {
        // 파일 ID로 강의 자료 조회
        CourseFile selectedFile = courseRepository.findFileById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파일 ID입니다."));

        // 파일이 해당 강의에 속하는지 확인
        if (!selectedFile.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("이 파일은 해당 강의에 속하지 않습니다.");
        }

        // 강의명 가져오기
        String courseName = selectedFile.getCourse().getTitle();

        // 퀴즈 제목 설정
        String quizTitle = "Quiz for " + courseName + " - " + selectedFile.getOriginalFilename();

        // 사용자 ID로 User 객체 조회하여 설정
        User user = userRepository.findById(quizRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 퀴즈 상태 설정 및 저장
        Quiz quiz = new Quiz();
        quiz.setQuizTitle(quizTitle);
        quiz.setQuizState(QuizState.IN_PROGRESS);
        quiz.setUser(user);
        quizRepository.save(quiz);

        // 퀴즈 개수 범위 처리 ("5-7" 형태로 받았을 때)
        String[] range = quizRequestDto.getQuizCountRange().split("-");
        int minQuestions = Integer.parseInt(range[0]);
        int maxQuestions = Integer.parseInt(range[1]);

        // minQuestions와 maxQuestions 사이에서 랜덤 값 생성
        Random random = new Random();
        int totalQuestions = random.nextInt(maxQuestions - minQuestions + 1) + minQuestions;

        // 파일에서 퀴즈 질문 생성 (세부 요구사항을 기반으로 질문을 생성할 때 활용)
        List<String> generatedQuestions = generateQuestionsFromFile(selectedFile, quizRequestDto.getDetailedRequirements());
        QuestionType questionType = quizRequestDto.getQuestionType();  // 이제 questionType은 QuestionType 타입입니다.

        // 퀴즈 질문을 저장
        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < totalQuestions; i++) {
            if (i < generatedQuestions.size()) {
                String questionContent = generatedQuestions.get(i);
                QuizQuestion question = new QuizQuestion();
                question.setQuestionContent(questionContent);
                question.setAnswer("Sample Answer");  // 이 부분을 실제로 작성한 답으로 설정해야 함
                question.setExplanation("This is a sample explanation.");
                question.setQuestionType(questionType);  // 변환된 QuestionType 설정
                question.setQuiz(quiz);
                questions.add(question);
            }
        }

        quizQuestionRepository.saveAll(questions);

        // 퀴즈 제목과 각 질문을 DTO에 담아서 반환
        return new QuizDetailResponseDto(
                quiz.getQuizId(),  // quizId를 추가
                quiz.getQuizTitle(),
                courseName,
                questions.stream()
                        .map(q -> new QuestionDetailDto(
                                q.getQuestionId(),
                                q.getQuestionContent(),
                                q.getQuizChoices() != null ? q.getQuizChoices().stream()
                                        .map(choice -> new QuizChoiceDto(null, choice.getChoiceContent()))  // choiceId는 null로 설정
                                        .collect(Collectors.toList()) : Collections.emptyList(),
                                q.getAnswer(),
                                q.getExplanation()
                        ))
                        .collect(Collectors.toList())

        );



    }


    // 3. 퀴즈 세부 정보 조회
    public QuizDetailResponseDto getQuizDetails(Long quizId) {
        // Repository에서 findQuizDetailsById 쿼리 메서드 호출
        List<QuestionDetailDto> questionDetails = quizRepository.findQuizDetailsById(quizId);

        // Quiz 엔티티를 가져오면서 강의 제목(courseTitle)을 가져옵니다.
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException());

        String courseTitle = quiz.getCourse().getTitle();

        // QuizDetailResponseDto 생성하여 반환
        return new QuizDetailResponseDto(
                quiz.getQuizId(),  // quizId를 추가
                quiz.getQuizTitle(),
                courseTitle,
                questionDetails  // findQuizDetailsById로 가져온 질문 세부 정보 사용
        );
    }

    // 4. 답안 제출
    @Transactional
    public SubmitQuizResponseDto submitAnswers(Long quizId, SubmitQuizRequestDto submissionRequestDto) {
        Long userId = submissionRequestDto.getUserId();  // 사용자 ID
        Long receivedQuizId = submissionRequestDto.getQuizId();  // 퀴즈 ID (요청 본문에서 가져오기)
        List<UserAnswerDto> userAnswers = submissionRequestDto.getAnswers();

        // 퀴즈가 존재하는지 확인
        Quiz quiz = quizRepository.findById(receivedQuizId)
                .orElseThrow(() -> new QuizNotFoundException());  // 퀴즈 ID로 퀴즈 조회

        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());  // 사용자 ID로 사용자 조회

        // 사용자 답안 저장
        List<UserAnswer> savedAnswers = userAnswers.stream().map(answerDto -> {
            QuizQuestion question = quizQuestionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new QuizQuestionNotFoundException());

            boolean isCorrect = question.getAnswer().equals(answerDto.getUserAnswer());

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setQuiz(quiz);  // Quiz 설정
            userAnswer.setUser(user);  // User 설정
            userAnswer.setQuizQuestion(question);  // QuizQuestion 설정
            userAnswer.setUserAnswer(answerDto.getUserAnswer());
            userAnswer.setCorrect(isCorrect);
            return userAnswer;
        }).collect(Collectors.toList());

        // 답안 저장
        userAnswerRepository.saveAll(savedAnswers);

        // SubmitQuizResponseDto 반환
        return new SubmitQuizResponseDto("Answers submitted successfully.", userAnswers);
    }


    // 5. 답안 수정
    @Transactional
    public SubmitQuizResponseDto updateAnswers(SubmitQuizRequestDto submissionRequestDto) {
        Long userId = submissionRequestDto.getUserId();  // 사용자 ID 가져오기
        Long receivedQuizId = submissionRequestDto.getQuizId();  // 퀴즈 ID 가져오기
        List<UserAnswerDto> userAnswers = submissionRequestDto.getAnswers();  // 답안 리스트

        // 퀴즈가 존재하는지 확인
        Quiz quiz = quizRepository.findById(receivedQuizId)
                .orElseThrow(() -> new QuizNotFoundException());  // 퀴즈 ID로 퀴즈 조회

        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());  // 사용자 ID로 사용자 조회

        // 사용자 답안 수정
        List<UserAnswer> updatedAnswers = userAnswers.stream().map(answerDto -> {
            QuizQuestion question = quizQuestionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new QuizQuestionNotFoundException());  // 해당 질문이 존재하지 않으면 예외 발생

            // 기존 답안이 있는지 확인
            Optional<UserAnswer> existingAnswer = userAnswerRepository.findByQuizQuestionAndQuizAndUserId(
                    question, quiz, userId);  // 사용자 ID로 기존 답안 찾기

            boolean isCorrect = question.getAnswer().equals(answerDto.getUserAnswer());

            UserAnswer userAnswer;
            if (existingAnswer.isPresent()) {
                // 기존 답안이 있으면 수정
                existingAnswer.get().setUserAnswer(answerDto.getUserAnswer());
                existingAnswer.get().setCorrect(isCorrect);
                userAnswer = existingAnswer.get();
            } else {
                // 기존 답안이 없으면 새로 생성
                userAnswer = new UserAnswer();
                userAnswer.setQuizQuestion(question);
                userAnswer.setUserAnswer(answerDto.getUserAnswer());
                userAnswer.setQuiz(quiz);
                userAnswer.setUser(user);
                userAnswer.setCorrect(isCorrect);
            }

            return userAnswer;
        }).collect(Collectors.toList());

        // 수정된 답안 저장
        userAnswerRepository.saveAll(updatedAnswers);

        // List<UserAnswer>를 List<UserAnswerDto>로 변환
        List<UserAnswerDto> updatedAnswerDtos = updatedAnswers.stream().map(answer ->
                new UserAnswerDto(answer.getQuizQuestion().getQuestionId(), answer.getUserAnswer())  // getId() -> getQuestionId()로 변경
        ).collect(Collectors.toList());

        // 수정된 답안 정보를 포함한 응답 반환
        return new SubmitQuizResponseDto("Answers updated successfully.", updatedAnswerDtos);
    }



    // 6. 채점 결과 조회
    public QuizResultResponseDto getQuizResult(Long quizId) {
        return quizResultService.getQuizResult(quizId);
    }

    // 7. 퀴즈 삭제
    @Transactional
    public void deleteQuiz(Long quizId) {
        // 퀴즈가 존재하는지 확인
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException());


        // 사용자 답안 삭제
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuiz(quiz);
        if (!userAnswers.isEmpty()) {
            userAnswerRepository.deleteAll(userAnswers);
        }

        // 퀴즈 질문 및 선택지 삭제
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuiz(quiz);
        if (!quizQuestions.isEmpty()) {
            // 퀴즈 질문 내의 선택지 삭제
            quizQuestions.forEach(question -> {
                if (!question.getQuizChoices().isEmpty()) {
                    question.getQuizChoices().forEach(choice -> quizChoiceRepository.delete(choice));
                    question.getQuizChoices().clear();
                }
            });
            // 퀴즈 질문 삭제
            quizQuestionRepository.deleteAll(quizQuestions);
        }

        // 퀴즈 삭제
        quizRepository.delete(quiz);
    }


    // 파일에서 질문을 생성하는 메서드 (OpenAI API 사용)
    private List<String> generateQuestionsFromFile(CourseFile courseFile, String detailedRequirements) {
        // OpenAI API를 통해 질문을 생성하는 로직
        String fileContent = extractTextFromPDF(courseFile.getFilePath());
        List<QuizQuestion> quizQuestions = openAIService.generateQuizQuestions(fileContent, detailedRequirements);

        // QuizQuestion에서 questionContent만 추출하여 List<String>으로 변환
        return quizQuestions.stream()
                .map(QuizQuestion::getQuestionContent)
                .collect(Collectors.toList());
    }


    // PDF 파일에서 텍스트 추출
    private String extractTextFromPDF(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            throw new RuntimeException("PDF 파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}
