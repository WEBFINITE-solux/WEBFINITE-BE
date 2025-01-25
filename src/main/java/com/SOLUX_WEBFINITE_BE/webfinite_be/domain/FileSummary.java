package com.SOLUX_WEBFINITE_BE.webfinite_be.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class FileSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    @Column(name = "summary_content", columnDefinition = "TEXT")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private CourseFile file;

    // ==== 연관 관계 메서드 ====
    // 로직 상에서 양쪽에 값을 세팅해주기 위함
    public void setFile(CourseFile file){
        this.file = file;
        file.setSummary(this);
    }


    // ==== 생성 메서드 ====
    public static FileSummary createSummary(String content){
        FileSummary fileSummary = new FileSummary();
        fileSummary.content = content;

        return fileSummary;
    }
}