package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

public class GeneratePrompt {

    public static String createPlanPrompt(String subjectName, String startDate, String endDate, String startUnit, String endUnit, String additionalPrompt, String fileText) {
        String template = subjectName +" 과목의 학습 계획을 한국어로 작성해줘. 학습 기간은 " + startDate + "부터 " + endDate + "까지이고 " +
                "각 주차별로 한개씩 학습 목표와 학습 내용을 세부적으로 작성해줘." +
                "주차수보다 단원수가 많을경우 주차별로 묶어서 작성해줘." +
                "학습 범위는 " + startUnit + "부터 " + endUnit + "까지야 " +
                additionalPrompt +
                "학습 계획에 사용할 참고 자료는" + fileText + "야." +
                "JSON 형식의 결과를 제외한 텍스트는 제외해줘." +
                "각 주차 예시) " +
                "{week: 1," +
                "plan_title: 1-4 행렬 연산," +
                "plan_description: 덧셈, 뺄셈, 스칼라 곱셈을 포함한 행렬 연산을 소개합니다.}";

        return template;
    }
}
