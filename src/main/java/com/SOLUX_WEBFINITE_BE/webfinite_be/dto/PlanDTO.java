package com.SOLUX_WEBFINITE_BE.webfinite_be.dto;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.LearningPlan;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlanDTO {
    private String promptText;
    private List<Plan> learningPlan;

    @Data
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Plan {
        private Long planId;
        private int week;
        private String planTitle;
        private String planDescription;
    }

    public static List<Plan> toPlanDTO(List<LearningPlan> learningPlans) {
        return learningPlans.stream().map(learningPlan -> new Plan(learningPlan.getId(), learningPlan.getWeek(), learningPlan.getTitle(), learningPlan.getDescription())).toList();
    }
}
