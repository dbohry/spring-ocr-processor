package com.lhamacorp.springocrtesseract.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

import static com.lhamacorp.springocrtesseract.Common.getPath;

@Slf4j
@Component
public class OcrProcessor {

    @Async
    public void process(String id, MultipartFile file, String language) throws IOException {
        try {
            log.info("Starting OCR processing for execution [{}]", id);
            byte[] fileContent = file.getBytes();

            try (InputStream fileInputStream = new ByteArrayInputStream(fileContent)) {
                String result = processImage(fileInputStream, language);
                log.info("OCR processing completed for execution [{}]", id);
                storeResult(id, result);
            }

        } catch (IOException | InterruptedException e) {
            log.error("An error occurred while processing execution [{}]", id, e);
            storeResult(id, "Failed");
        }
    }

    public String processImage(InputStream inputStream, String language) throws IOException, InterruptedException {
        StringBuilder ocrResult = new StringBuilder();
        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("ocr_input_" + UUID.randomUUID(), ".tmp");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            String command = String.format("tesseract %s stdout -l %s", tempFile.toAbsolutePath(), language);
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

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

    private void storeResult(String id, String result) throws IOException {
        Path outputDir = Paths.get(getPath(id));
        Files.createDirectories(outputDir);
        Path outputFilePath = outputDir.resolve("output.txt");
        Files.write(outputFilePath, result.getBytes(), StandardOpenOption.CREATE);
    }

}