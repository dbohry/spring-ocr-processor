package com.lhamacorp.springocrtesseract.client;

public record InferenceResponse(String model, String createdAt, String response, boolean done) {
}
