package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

public class GeneratePrompt {

    public static String createPlanPrompt(String subjectName, String startDate, String endDate, String startUnit, String endUnit, String additionalPrompt, String fileText) {
        String template =
                "이전에 세웠던 학습 계획과 프롬프트는 모두 무시해줘 " +
                "너는 학습 일정과 단원 범위, 추가 요구사항, 참고 자료를 받아 학습 계획을 작성헤주는 역할이야. " +
                "추가 요구사항은 필수가 아니라서 비어있을 수도 있어. " +
                "주의 사항을 반드시 준수하여 작성해줘. " +
                subjectName +" 과목의 학습 계획을 한국어로 작성해줘. " +
                "학습 기간: " + startDate + " ~  " + endDate + ", " +
                "학습 범위: " + startUnit + " ~ " + endUnit + ". " +
                "추가 요구사항: " + additionalPrompt + ", " +
                "학습 계획에 사용할 참고 자료는" + fileText + "야." +
                "주의 사항: " +
                "1. 학습 계획은 반드시 학습 기간을 초과하지 않도록 작성해줘. " +
                "   학습 기간은 절대적인 기준이며, 이를 넘어가는 주차를 생성하지 말아줘. " +
                "2. 학습 기간 내에서 주차별 계획을 작성할 때, 단원 범위가 학습 기간의 주차 수보다 많을 경우 한 주에 여러 단원을 포함하도록 조정해줘. " +
                "3. 각 주차는 기본적으로 정확히 7일 기준으로 나눠야 해. 하지만 학습 기간이 7일로 나누어떨어지지 않을 경우: " +
                "   - 남은 기간이 4일 이하라면 해당 기간은 계획에서 제외해줘. " +
                "   - 남은 기간이 5일 이상이면 추가로 한 주를 생성해줘. " +
                "4. 결과에 `week`가 동일한 객체가 여러 개 존재하지 않도록 각 주차는 하나의 객체로 묶어줘. " +
                "5. JSON 형식의 결과를 제외한 모든 텍스트는 생략해줘. " +
                "   결과의 맨 앞부분의 ```JSON과 맨 끝의 ```도 제거한 상태로 List로 반환해줘. " +
                "   JSON 매핑할 때 오류가 발생하지 않도록 JSON 형식이 맞는지 반드시 확인해줘. " +
                "6. 학습 계획 작성 시 학습 기간과 범위가 적절히 분배되었는지 반드시 확인해줘. " +
                "   학습 기간을 벗어나는 경우 요청은 잘못된 것으로 간주하고, 반드시 기간 안에서 해결해야 해. " +
                "각 주차 예시) " +
                "{week: 1," +
                "plan_title: 1-4 행렬 연산," +
                "plan_description: 덧셈, 뺄셈, 스칼라 곱셈을 포함한 행렬 연산을 소개합니다.}";

        return template;
    }
}
