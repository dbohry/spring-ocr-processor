package com.lhamacorp.springocrtesseract.service;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Slf4j
public class OcrProcessor {

    public static void executeTesseract(String id, String imagePath, String outputFilePath, String language) throws IOException, InterruptedException {
        try {
            String command = String.format("tesseract %s %s -l %s", imagePath, outputFilePath, language);

            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                log.info("Ocr execution successful [{}]", id);
            } else {
                log.error("Ocr execution failed [{}]", id);
                throw new Exception(id);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to process image [{}]", id, e);
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args) {
        String id = UUID.randomUUID().toString();
        String imagePath = "./files/IMG_6599.jpeg";
        String outputFilePath = "./results/" + id;
        String language = "deu";

        try {
            executeTesseract(id, imagePath, outputFilePath, language);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}