package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.service.GPTService;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.PlanService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final GPTService gptService;

    @PostMapping("/{courseId}/new")
    public String createPlan(@PathVariable("courseId") Long courseId, @RequestBody CreatePlanRequest request) throws IOException, URISyntaxException {
        return planService.createPlan(courseId, request.promptText, request.startDate, request.endDate, request.startUnit, request.endUnit, request.fileId);
    }


    @Data
    @AllArgsConstructor
    static class CreatePlanRequest {
        @JsonProperty("prompt_text")
        private String promptText;
        @JsonProperty("start_date")
        private LocalDate startDate;
        @JsonProperty("end_date")
        private LocalDate endDate;
        @JsonProperty("start_unit")
        private String startUnit;
        @JsonProperty("end_unit")
        private String endUnit;
        @JsonProperty("file_id")
        private Long fileId;
    }
}
