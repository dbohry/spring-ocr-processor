package com.lhamacorp.springocrtesseract.api;

import com.lhamacorp.springocrtesseract.api.dto.JobResult;
import com.lhamacorp.springocrtesseract.api.dto.OcrResult;
import com.lhamacorp.springocrtesseract.service.OcrService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class OcrController {

    private final OcrService service;

    @PostMapping
    public ResponseEntity<JobResult> processImage(@RequestPart(value = "file") MultipartFile file) {
        JobResult response = new JobResult(service.processImage(file));
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<OcrResult> getResult(@PathVariable String id) {
        OcrResult response = new OcrResult(service.findProcess(id));
        return ResponseEntity.ok(response);
    }

}
