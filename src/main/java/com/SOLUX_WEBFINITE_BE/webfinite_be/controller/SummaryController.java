package com.SOLUX_WEBFINITE_BE.webfinite_be.controller;

import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SimpleResponse;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SummaryDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.service.SummaryService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/summary")
public class SummaryController {
    private final SummaryService summaryService;

    // 요약 파일 조회
    @GetMapping("/{fileId}")
    public SummaryDTO getSummary(@PathVariable("fileId") Long fileId) {
        return summaryService.getSummary(fileId);
    }

    // 요약 생성
    @PostMapping("/{fileId}/new")
    public SimpleResponse createSummary(@PathVariable("fileId") Long fileId) throws IOException {
        summaryService.createSummary(fileId);
        return new SimpleResponse("요약 생성 완료");
    }
}
