# LearnAIble Backend

> AI 기반 학습관리 플랫폼 **LearnAIble**의 백엔드 저장소입니다.
> 사용자의 입력을 기반으로 맞춤형 학습계획을 제공하고, 퀴즈·요약·할 일 관리 등 다양한 기능을 통해 효율적인 학습을 지원합니다.

## 📌 Project Introduction

LearnAIble은 학습자가 자신의 학습 목표와 상황을 입력하면 AI를 활용해 개인 맞춤형 학습계획을 제공하는 학습관리 플랫폼입니다.

백엔드에서는 사용자 인증을 기반으로 학습계획, 강의, 퀴즈, 요약, 할 일 및 출석과 관련된 기능을 API로 제공합니다.

## ✨ Features

* **사용자 인증 및 인가**

  * JWT 기반 인증
  * 비밀번호 암호화
* **학습계획 관리**

  * 사용자 맞춤형 학습계획 생성 및 관리
* **강의 관리**

  * 강의 및 학습 콘텐츠 관리
* **AI 학습 지원**

  * OpenAI API를 활용한 학습계획 생성
  * 학습 내용 요약
  * 학습 자료 기반 퀴즈 생성 및 관리
* **학습 관리**

  * 할 일 관리
  * 출석 관리
* **API 문서화**

  * Swagger를 활용한 API 명세 확인

## 🛠 Tech Stack

### Backend

| Category          | Technology        |
| ----------------- | ----------------- |
| Language          | Java 17           |
| Framework         | Spring Boot 3.4.1 |
| Build Tool        | Gradle            |
| ORM               | Spring Data JPA   |
| Database          | H2                |
| Security          | Spring Security   |
| Authentication    | JWT               |
| API Documentation | Swagger / OpenAPI |
| AI                | OpenAI API        |
| PDF Processing    | Apache PDFBox     |
| Utility           | Lombok            |

## 📂 Package Structure

```text
src/main/java/com/SOLUX_WEBFINITE_BE/webfinite_be
├── auth
│   ├── JwtAuthenticationFilter
│   ├── JwtToken
│   └── JwtTokenProvider
├── common
│   ├── CustomException
│   ├── ErrorCode
│   ├── ErrorResponse
│   └── GlobalExceptionHandler
├── config
│   ├── OpenAIConfig
│   ├── SecurityConfig
│   ├── SwaggerConfig
│   └── WebConfig
├── controller
│   ├── AttendController
│   ├── CourseController
│   ├── PlanController
│   ├── QuizController
│   ├── SummaryController
│   └── TodoController
└── WebfiniteBeApplication
```

## 🔐 Authentication

Spring Security와 JWT를 활용하여 사용자 인증 기능을 구현했습니다.

* JWT 토큰 발급 및 검증
* 인증 필터를 통한 요청 검증
* 비밀번호 암호화
* 인증이 필요한 API 접근 제어

## 🤖 AI 기능

OpenAI API를 연동하여 학습 과정에서 필요한 AI 기능을 제공합니다.

* 사용자 입력 기반 맞춤형 학습계획 생성
* 학습자료 요약
* 학습 내용 기반 퀴즈 생성

