package com.lhamacorp.springocrtesseract.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static com.lhamacorp.springocrtesseract.service.OcrProcessor.executeTesseract;

@Slf4j
@Service
public class OcrService {

    public String process() {
        String id = UUID.randomUUID().toString();
        String imagePath = "./files/IMG_6599.jpeg";
        String outputFilePath = "./results/" + id;
        String language = "deu";

        executeTesseract(id, imagePath, outputFilePath, language);

        return id;
    }

    public String processImage(MultipartFile file) {
        String id = UUID.randomUUID().toString();
        String uploadDir = "./files/" + id;
        String imagePath = uploadDir + "/" + file.getOriginalFilename();
        String outputFilePath = uploadDir + "/output";
        String language = "deu";

        new File(uploadDir).mkdirs();

        try {
            Path path = Paths.get(imagePath);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            executeTesseract(id, imagePath, outputFilePath, language);
            return id;
        } catch (IOException e) {
            System.err.println("An error occurred while processing file " + id + ": " + e.getMessage());
            return "Failed";
        }
    }

    @SneakyThrows
    public String findProcess(String id) {
        String filePath = "./files/" + id + "/output.txt";
        return Files.readString(Paths.get(filePath));
    }

}
