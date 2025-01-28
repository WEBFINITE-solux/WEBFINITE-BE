package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.*;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private final FileRepository fileRepository;

    @Autowired
    public QuizService(UserRepository userRepository,
                       QuizRepository quizRepository,
                       QuizQuestionRepository quizQuestionRepository,
                       CourseRepository courseRepository,
                       UserAnswerRepository userAnswerRepository,
                       QuizResultService quizResultService,
                       QuizChoiceRepository quizChoiceRepository,
                       @Lazy OpenAIService openAIService, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.courseRepository = courseRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.quizResultService = quizResultService;
        this.quizChoiceRepository = quizChoiceRepository;
        this.openAIService = openAIService;
        this.fileRepository = fileRepository;
    }


    // 1. 생성된 퀴즈 목록 조회
    public List<QuizListResponseDto> getQuizzesByUserAndCourse(Long userId, Long courseId) {
        // 사용자 ID 유효성 검사
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());

        // 강의 ID 유효성 검사
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException());
        List<Quiz> quizzes = quizRepository.findByUser_IdAndCourse_Id(userId, courseId);

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
        CourseFile selectedFile = fileRepository.findFileById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파일 ID입니다."));

        if (!selectedFile.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("이 파일은 해당 강의에 속하지 않습니다.");
        }

        String courseName = selectedFile.getCourse().getTitle();
        String quizTitle = "Quiz for " + courseName + " - " + selectedFile.getOriginalFilename();

        User user = userRepository.findById(quizRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 강의 ID입니다."));

        Quiz quiz = new Quiz();
        quiz.setQuizTitle(quizTitle);
        quiz.setQuizState(QuizState.IN_PROGRESS);
        quiz.setUser(user);
        quiz.setCourse(course);
        quiz.setCourseFile(selectedFile);

        quizRepository.save(quiz);

        String[] range = quizRequestDto.getQuizCountRange().split("-");
        int minQuestions = Integer.parseInt(range[0]);
        int maxQuestions = Integer.parseInt(range[1]);
        Random random = new Random();
        int totalQuestions = random.nextInt(maxQuestions - minQuestions + 1) + minQuestions;

        List<QuizQuestion> generatedQuestions = openAIService.generateQuizQuestions(
                selectedFile,
                quizRequestDto.getDetailedRequirements(),
                quizRequestDto.getQuestionType(),
                totalQuestions
        );

        // 부모-자식 관계 설정
        for (QuizQuestion question : generatedQuestions) {
            question.setQuiz(quiz); // 퀴즈 설정
            for (QuizChoice choice : question.getQuizChoices()) {
                // 이미 부모-자식 관계가 설정된 경우 추가하지 않음
                if (choice.getQuizQuestion() == null) {
                    question.addQuizChoice(choice);
                }
            }
        }

        // 퀴즈 질문 저장 (CascadeType.ALL로 자식도 저장됨)
        quizQuestionRepository.saveAll(generatedQuestions);
        System.out.println("Quiz count range: " + quizRequestDto.getQuizCountRange());
        System.out.println("Total questions to generate: " + totalQuestions);
        System.out.println("Generated questions count: " + generatedQuestions.size());
        for (QuizQuestion question : generatedQuestions) {
            System.out.println("Question: " + question.getQuestionContent());
            System.out.println("Choices count: " + question.getQuizChoices().size());
        }


        return new QuizDetailResponseDto(
                quiz.getQuizId(),
                quiz.getQuizTitle(),
                courseName,
                quiz.getQuizType() != null ? quiz.getQuizType().name() : "",  // quizType을 문자열로 변환하여 전달
                generatedQuestions.stream()
                        .map(q -> new QuestionDetailDto(
                                q.getQuestionId(),
                                q.getQuestionContent(),
                                q.getQuizChoices() != null ? q.getQuizChoices().stream()
                                        .map(choice -> new QuizChoiceDto(choice.getChoiceId(), choice.getChoiceContent()))
                                        .collect(Collectors.toList()) : Collections.emptyList(),
                                q.getAnswer(),
                                q.getExplanation(),
                                q.getQuestionType()
                        ))
                        .collect(Collectors.toList())
        );
    }


    // 3. 퀴즈 세부 정보 조회
    public QuizDetailResponseDto getQuizDetails(Long quizId) {
        // Quiz 엔티티를 가져오면서 강의 제목(courseTitle)을 가져옵니다.
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException());

        String courseTitle = quiz.getCourse().getTitle();

        // QuizQuestion 데이터를 조회하여 QuestionDetailDto로 변환
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuiz_QuizId(quizId);

        // 모든 질문의 questionType을 확인
        QuestionType quizType = null;
        for (QuizQuestion question : quizQuestions) {
            if (quizType == null) {
                quizType = question.getQuestionType();  // 첫 번째 질문의 questionType을 설정
            } else if (!quizType.equals(question.getQuestionType())) {
                quizType = null;  // 만약 질문들이 서로 다른 type을 가지면 quizType은 null로 설정
                break;
            }
        }

        List<QuestionDetailDto> questionDetails = quizQuestions.stream()
                .map(question -> new QuestionDetailDto(
                        question.getQuestionId(),
                        question.getQuestionContent(),
                        question.getQuizChoices() != null ? question.getQuizChoices().stream()
                                .map(choice -> new QuizChoiceDto(choice.getChoiceId(), choice.getChoiceContent()))
                                .collect(Collectors.toList()) : Collections.emptyList(),
                        question.getAnswer(),
                        question.getExplanation(),
                        question.getQuestionType()
                ))
                .collect(Collectors.toList());

        // QuizDetailResponseDto 생성하여 반환
        return new QuizDetailResponseDto(
                quiz.getQuizId(),
                quiz.getQuizTitle(),
                courseTitle,
                quizType.name(),  // quizType 값을 이름으로 반환
                questionDetails
        );
    }


    // 4. 답안 제출
    @Transactional
    public SubmitQuizResponseDto submitAnswers(SubmitQuizRequestDto submissionRequestDto) {
        Long userId = submissionRequestDto.getUserId();  // 사용자 ID
        Long quizId = submissionRequestDto.getQuizId();  // 퀴즈 ID
        List<UserAnswerDto> userAnswers = submissionRequestDto.getAnswers();

        // 퀴즈가 존재하는지 확인
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException());  // 퀴즈 ID로 퀴즈 조회

        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());  // 사용자 ID로 사용자 조회

        // 이미 답안을 제출했는지 확인
        boolean alreadySubmitted = userAnswerRepository.existsByQuizAndUser(quiz, user);
        if (alreadySubmitted) {
            throw new AlreadySubmittedException();  // 메시지 생략
        }

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

        // 퀴즈 상태를 COMPLETED로 변경
        quiz.setQuizState(QuizState.COMPLETED);  // 상태 변경
        quizRepository.save(quiz);  // 상태 변경 후 퀴즈 저장

        // SubmitQuizResponseDto 반환
        return new SubmitQuizResponseDto("답안이 성공적으로 제출되었습니다.", userAnswers);
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
            Optional<UserAnswer> existingAnswer = userAnswerRepository.findByQuizQuestionAndQuizAndUser_Id(
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

        quiz.setQuizState(QuizState.COMPLETED);  // 상태 변경
        quizRepository.save(quiz);  // 상태 변경 후 퀴즈 저장

        // List<UserAnswer>를 List<UserAnswerDto>로 변환
        List<UserAnswerDto> updatedAnswerDtos = updatedAnswers.stream().map(answer ->
                new UserAnswerDto(answer.getQuizQuestion().getQuestionId(), answer.getUserAnswer())  // getId() -> getQuestionId()로 변경
        ).collect(Collectors.toList());

        // 수정된 답안 정보를 포함한 응답 반환
        return new SubmitQuizResponseDto("답안이 성공적으로 수정되었습니다.", updatedAnswerDtos);
    }



    // 6. 채점 결과 조회
    public QuizResultResponseDto getQuizResult(Long quizId) {
        // 퀴즈가 존재하는지 확인 (존재하지 않으면 예외 발생)
        quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException()); // 퀴즈 ID로 퀴즈 조회
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


    // 파일에서 퀴즈 질문 생성 로직 (OpenAI API 사용) -> 매개변수 하나 지우고, fileContent 반환하도록 변경, 나머지 코드 지우기
    String generateFileContent(CourseFile courseFile) {
        String fileContent = extractTextFromPDF(courseFile.getFilePath());
        return fileContent;
    }


    // PDF 파일에서 텍스트 추출
    private String extractTextFromPDF(String filePath) {
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            // 텍스트 정리 및 검증
            text = text.trim(); // 공백 제거
            if (text.isEmpty()) {
                throw new RuntimeException("추출된 텍스트가 비어 있습니다.");
            }

            System.out.println("Extracted text (first 100 chars): " + text.substring(0, Math.min(text.length(), 100)));
            return text;
        } catch (IOException e) {
            throw new RuntimeException("PDF 파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }


}
