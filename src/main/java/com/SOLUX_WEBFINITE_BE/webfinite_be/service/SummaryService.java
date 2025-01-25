package com.SOLUX_WEBFINITE_BE.webfinite_be.service;

import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.CourseFile;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.FileSummary;
import com.SOLUX_WEBFINITE_BE.webfinite_be.domain.Prompt;
import com.SOLUX_WEBFINITE_BE.webfinite_be.dto.SummaryDTO;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.FileNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.exception.SummaryNotFoundException;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.FileRepository;
import com.SOLUX_WEBFINITE_BE.webfinite_be.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SummaryService {
    private final SummaryRepository summaryRepository;
    private final FileRepository fileRepository;
    private final GPTService gptService;

    public SummaryDTO getSummary(Long fileId) {
        FileSummary summary = summaryRepository.findByFileId(fileId).orElseThrow(() -> new SummaryNotFoundException());
        return new SummaryDTO(summary.getId(), summary.getContent());
    }

    public void createSummary(Long fileId) throws IOException {
        CourseFile courseFile = fileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException());
        String filePath = courseFile.getFilePath();
        File file = new File(filePath);

        String getText = "";

        // 파일이 pdf면 텍스트 추출, txt면 그대로 읽기
        if(file.getName().endsWith(".txt")){
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            getText = sb.toString();
        }
        else if(file.getName().endsWith(".pdf")){
            getText = pdfToText(file);
        }
        else {
            throw new IOException("지원하지 않는 파일 형식입니다.");
        }

        String summaryPrompt = "이전 프롬프트를 무시하고, 다음 텍스트들의 요약을 작성해줘." +
                "제목이나 서론, 결론 없이 텍스트로된 본문만 보내줘" +
                "요약은 한국어로 해줘.\n" + getText;

        String response = gptService.chat(summaryPrompt);

        FileSummary summary = FileSummary.createSummary(response);
        summary.setFile(courseFile);

        summaryRepository.save(summary);
    }

    private String pdfToText(File file) throws IOException {
        try {
            PDDocument pdfDoc = PDDocument.load(file);
            String text = new PDFTextStripper().getText(pdfDoc);
            return text;
        }catch (IOException e){
            throw new IOException("파일을 읽을 수 없습니다.");
        }
    }
}
