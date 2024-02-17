package com.lhamacorp.springocrtesseract.service;

import com.lhamacorp.springocrtesseract.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class OcrService {

    private final OcrProcessor processor;

    private static final String DEFAULT_LANG = "deu";
    private static final String DEFAULT_DIR = "./files/";

    public String triggerProcess(MultipartFile file, String language) {
        String executionId = UUID.randomUUID().toString();

        new Thread(() -> {
            try {
                processImage(executionId, file, validateAndGetLanguage(language));
            } catch (Exception e) {
                log.error("An error occurred while processing execution [{}]", executionId, e);
            }
        }).start();

        return executionId;
    }

    public void processImage(String id, MultipartFile file, String language) {
        String imagePath = getPath(id) + "/" + file.getOriginalFilename();

        new File(getPath(id)).mkdirs();

        try {
            log.info("Store local files for execution [{}]", id);
            Path path = Paths.get(imagePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            log.info("Starting OCR process for execution [{}]", id);
            String result = processor.process(imagePath, language);

            log.info("Saving OCR result for execution [{}]", id);
            storeResult(id, result);
        } catch (IOException | InterruptedException e) {
            log.error("An error occurred while processing execution [{}]", id, e);
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
        Path outputFilePathObj = Paths.get(getPath(id) + "/output.txt");
        Files.write(outputFilePathObj, result.getBytes(), StandardOpenOption.CREATE);
    }

    private String getPath(String id) {
        return DEFAULT_DIR + id;
    }

}
