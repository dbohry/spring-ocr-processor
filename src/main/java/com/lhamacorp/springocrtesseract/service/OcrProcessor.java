package com.lhamacorp.springocrtesseract.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
public class OcrProcessor {

    public String process(InputStream inputStream, String language) throws IOException, InterruptedException {
        StringBuilder ocrResult = new StringBuilder();
        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("ocr_input_" + UUID.randomUUID(), ".tmp");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String command = String.format("tesseract %s stdout -l %s", tempFile.toAbsolutePath(), language);
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ocrResult.append(line).append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                log.info("OCR execution successful");
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                StringBuilder errorMessage = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMessage.append(errorLine).append("\n");
                }
                log.error("OCR execution failed. Error: {}", errorMessage);
                throw new RuntimeException("OCR execution failed with error: " + errorMessage);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to process image.", e);
            throw e;
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.error("Failed to delete temporary file.", e);
                }
            }
        }

        return ocrResult.toString();
    }

}