package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.service.GPTService;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.PlanService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final GPTService gptService;

    @PostMapping("/{courseId}/new")
    public Map<String, String> createPlan(@PathVariable("courseId") Long courseId, @RequestBody CreatePlanRequest request) throws IOException, URISyntaxException {
        return planService.createPlan(courseId, request.promptText, request.startDate, request.endDate, request.startUnit, request.endUnit, request.fileId);
    }


    @Data
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class CreatePlanRequest {
        private String promptText;
        private LocalDate startDate;
        private LocalDate endDate;
        private String startUnit;
        private String endUnit;
        private Long fileId;
    }
}
