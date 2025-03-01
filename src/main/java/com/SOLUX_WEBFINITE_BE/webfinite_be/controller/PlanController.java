package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.PlanDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SimpleResponse;
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

    @GetMapping("/{courseId}")
    public PlanDTO getPlan(@PathVariable("courseId") Long courseId) {
        return planService.getPlan(courseId);
    }

    @PostMapping("/{courseId}/new")
    public SimpleResponse createPlan(@PathVariable("courseId") Long courseId, @RequestBody CreatePlanRequest request) throws IOException, URISyntaxException {
        if(request == null){
            throw new IllegalStateException("학습 계획 정보가 없습니다.");
        }
        return planService.createPlan(courseId, request.promptText, request.startDate, request.endDate, request.startUnit, request.endUnit, request.fileId);
    }

    @PatchMapping("{courseId}/update")
    public SimpleResponse updatePlan(@PathVariable("courseId") Long courseId, @RequestBody PlanDTO plans){
        return planService.updatePlan(courseId, plans);
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
