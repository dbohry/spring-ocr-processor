package com.lhamacorp.springocrtesseract.api;

import com.lhamacorp.springocrtesseract.api.dto.JobResult;
import com.lhamacorp.springocrtesseract.api.dto.OcrResult;
import com.lhamacorp.springocrtesseract.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("ocr")
@AllArgsConstructor
public class OcrController {

    private final OcrService service;

    @PostMapping
    @Operation(summary = "Process an image for OCR",
        requestBody = @RequestBody(content = @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = MultipartFile.class))),
        responses = {
            @ApiResponse(responseCode = "202", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JobResult.class))),
        })
    public ResponseEntity<JobResult> processImage(@RequestPart(value = "file") MultipartFile file, @RequestParam(name = "lang", required = false) String lang) throws IOException {
        JobResult response = new JobResult(service.triggerProcess(file, lang));
        return ResponseEntity.status(ACCEPTED).body(response);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get OCR processing result by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OcrResult.class))),
            @ApiResponse(responseCode = "404", description = "Result not found")
        })
    public ResponseEntity<OcrResult> getResult(@PathVariable String id) {
        OcrResult response = new OcrResult(service.findProcess(id));
        return ResponseEntity.ok(response);
    }

}
