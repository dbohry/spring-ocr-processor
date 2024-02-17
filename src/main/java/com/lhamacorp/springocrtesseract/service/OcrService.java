package com.lhamacorp.springocrtesseract.service;

import com.lhamacorp.springocrtesseract.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@AllArgsConstructor
public class OcrService {

    private final OcrProcessor processor;

    private static final String DEFAULT_LANG = "deu";
    private static final String DEFAULT_DIR = "./files/";

    public String triggerProcess(MultipartFile file, String language) throws IOException {
        String executionId = UUID.randomUUID().toString();
        processImage(executionId, file, validateAndGetLanguage(language));
        return executionId;
    }

    @Async
    public void processImage(String id, MultipartFile file, String language) throws IOException {
        try {
            log.info("Starting OCR processing for execution [{}]", id);
            byte[] fileContent = file.getBytes();

            try (InputStream fileInputStream = new ByteArrayInputStream(fileContent)) {
                String result = processor.process(fileInputStream, language);
                log.info("OCR processing completed for execution [{}]. Saving result...", id);
                storeResult(id, result);
            }

        } catch (IOException | InterruptedException e) {
            log.error("An error occurred while processing execution [{}]", id, e);
            storeResult(id, "Failed");
        }
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

    private void storeResult(String id, String result) throws IOException {
        new File(getPath(id)).mkdirs();
        Path outputFilePathObj = Paths.get(getPath(id) + "/output.txt");
        Files.write(outputFilePathObj, result.getBytes(), StandardOpenOption.CREATE);
    }

    private String getPath(String id) {
        return DEFAULT_DIR + id;
    }

}
