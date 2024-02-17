package com.lhamacorp.springocrtesseract.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Component
public class OcrProcessor {

    public String process(String imagePath, String language) throws IOException, InterruptedException {
        StringBuilder ocrResult = new StringBuilder();

        try {
            String command = String.format("tesseract %s stdout -l %s", imagePath, language);

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ocrResult.append(line).append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                log.info("Ocr execution successful");
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                StringBuilder errorMessage = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMessage.append(errorLine).append("\n");
                }
                log.error("Ocr execution failed. Error: {}", errorMessage);
                throw new RuntimeException("OCR execution failed with error: " + errorMessage);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to process image.", e);
            throw e;
        }

        return ocrResult.toString();
    }

}