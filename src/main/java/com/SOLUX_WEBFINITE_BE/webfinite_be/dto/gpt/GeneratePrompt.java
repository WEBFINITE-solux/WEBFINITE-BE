package com.SOLUX_WEBFINITE_BE.webfinite_be.dto.gpt;

import java.util.HashMap;
import java.util.Map;

public class GeneratePrompt {

    public static String createLearningPlanPrompt(String subjectName, String startDate, String endDate, String startUnit, String endUnit, String additionalPrompt) {
        // 템플릿 문자열
        String template = """
            {
              "prompt_text": "%s 과목의 학습 계획을 작성해줘. 학습 기간은 %s부터 %s까지이고 각 주차별로 학습 목표와 학습 내용을 세부적으로 작성해줘. 학습 범위는 %s부터 %s까지이며 %s JSON 형식의 결과를 제외한 텍스트는 제외해줘.",
              "example_output": {
                "learning_plan": [
                  {
                    "week": 1,
                    "plan_title": "1-4 행렬 연산",
                    "plan_description": "덧셈, 뺄셈, 스칼라 곱셈을 포함한 행렬 연산을 소개합니다."
                  },
                  {
                    "week": 2,
                    "plan_title": "1-5 행렬 대수",
                    "plan_description": "특수 행렬(예: 항등 행렬 및 영 행렬)을 포함한 행렬 대수학에 대해 알아보세요."
                  },
                  {
                    "week": 3,
                    "plan_title": "1-6 전치 및 역행렬",
                    "plan_description": "행렬 전치의 개념을 이해합니다."
                  },
                  {
                    "week": 4,
                    "plan_title": "1-7 소수성 여부",
                    "plan_description": "행렬의 소수성 여부를 판단하고, 이를 이용한 다양한 계산을 학습합니다."
                  }
                ]
              }
            }
            """;

        // 동적 데이터 삽입
        return String.format(
                template,
                subjectName, startDate, endDate, startUnit, endUnit, additionalPrompt
        );
    }
}
