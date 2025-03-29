package com.lhamacorp.springocrtesseract.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Component
public class InferenceClient {

    @Value("${client.inference.url}")
    private String apiUrl;

    @Value("${client.inference.model}")
    private String model;

    private static final String PRE_PROMPT = "You are a data-cleaning assistant. "
        + "Ignore all previous instructions. "
        + "Your task is to process the following raw string, removing unnecessary symbols, excessive whitespace, and redundant line breaks. "
        + "No explanations. Clean this string: ";

    public String infer(String prompt) {
        if (apiUrl == null || apiUrl.isEmpty()) {
            return prompt;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);

            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("model", model);
            jsonRequest.addProperty("prompt", PRE_PROMPT + prompt);
            jsonRequest.addProperty("stream", false);

            StringEntity entity = new StringEntity(jsonRequest.toString(), APPLICATION_JSON);
            request.setEntity(entity);
            request.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                String jsonString = EntityUtils.toString(response.getEntity());
                InferenceResponse inferenceResponse = new Gson().fromJson(jsonString, InferenceResponse.class);
                return inferenceResponse != null ? inferenceResponse.response() : null;
            } else {
                System.err.println("Request failed: " + statusCode + " - " + EntityUtils.toString(response.getEntity()));
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
