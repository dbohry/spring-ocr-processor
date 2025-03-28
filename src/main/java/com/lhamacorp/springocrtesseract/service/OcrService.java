package com.lhamacorp.springocrtesseract.service;

import com.lhamacorp.springocrtesseract.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static com.lhamacorp.springocrtesseract.Common.DEFAULT_LANG;
import static com.lhamacorp.springocrtesseract.Common.getPath;

@Slf4j
@Service
@AllArgsConstructor
public class OcrService {

    private final OcrProcessor processor;

    public String triggerProcess(MultipartFile file, String language) throws IOException {
        String executionId = UUID.randomUUID().toString();
        processor.process(executionId, file, validateAndGetLanguage(language));
        return executionId;
    }

    public String findProcess(String id) {
        try {
            String filePath = getPath(id) + "/output.txt";
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            log.warn("Ocr result not found for execution [{}]", id);
            throw new NotFoundException("Ocr result not found");
        }

    }

    private String validateAndGetLanguage(String language) {
        if (language == null) return DEFAULT_LANG;

        return switch (language.toLowerCase()) {
            case "eng", "deu" -> language.toLowerCase();
            default -> DEFAULT_LANG;
        };
    }

}
