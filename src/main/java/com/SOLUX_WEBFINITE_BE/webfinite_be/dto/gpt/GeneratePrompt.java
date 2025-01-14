package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

public class GeneratePrompt {

    public static String createPlanPrompt(String subjectName, String startDate, String endDate, String startUnit, String endUnit, String additionalPrompt, String fileText) {
        String template =
                "이전의 프롬프트는 모두 무시해줘 " +
                "너는 학습 일정과 단원 범위, 추가 요구사항, 참고 자료를 받아 학습 계획을 작성헤주는 역할이야. " +
                "추가 요구사항은 필수가 아니라서 비어있을 수도 있어. " +
                "주의 사항을 꼭 지켜서 작성해줘" +
                subjectName +" 과목의 학습 계획을 한국어로 작성해줘. 학습 기간: " + startDate + " ~  " + endDate + ", " +
                "학습 범위: " + startUnit + " ~ " + endUnit + ". " +
                "추가 요구사항: " + additionalPrompt + ", " +
                "학습 계획에 사용할 참고 자료는" + fileText + "야." +
                "주의 사항: " +
                "각 주차별로 학습 목표와 학습 내용을 세부적으로 작성해주고 같은 주차끼리 묶어줘. " +
                "단원수가 학습 기간의 주차수보다 많을 경우 한 주에 여러 단원을 넣어서라도 꼭 학습 기간을 맞춰줘. " +
                "각 주를 나누는 기준은 7일이야. " +
                "JSON 형식의 결과를 제외한 텍스트는 제외해줘. " +
                "결과 맨 앞부분의 ```JSON과 맨 끝의 ```도 제외하고 List로 반환해줘. " +
                "각 주차 예시) " +
                "{week: 1," +
                "plan_title: 1-4 행렬 연산," +
                "plan_description: 덧셈, 뺄셈, 스칼라 곱셈을 포함한 행렬 연산을 소개합니다.}";

        return template;
    }
}
