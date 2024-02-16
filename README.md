# Spring OCR Service API

This is a example of SpringBoot service that uses Tesseract OCR engine to provide a RESTful interface for processing images to extract text. It supports uploading images for OCR processing and retrieving the processed results by unique job IDs.

## Getting Started with Docker

### Building and Running the Service

To get the service up and running with Docker, follow these simple steps:

1. **Build the project:**

    ```shell
    ./gradlew clean build
    ```

2. **Build the Docker image:**

    ```shell
    docker build -t spring-ocr:latest .
    ```

3. **Run the container:**

    ```shell
    docker run --rm --name ocr -p 8080:8080 spring-ocr:latest
    ```

### Using the Service

#### Uploading an Image for OCR Processing

- **Command:**

    ```shell
    curl --location --request POST 'http://localhost:8080/ocr' \
    --form 'file=@"/path/to/your/ausweis.jpg"'
    ```

- **Expected HTTP Response:** `202 Accepted`

- **Response Body:**

    ```json
    {"id":"3a2f0d08-250d-438e-8c89-dc7ad854ef80"}
    ```

#### Retrieving OCR Results

- **Command:**

    ```shell
    curl --location --request GET 'http://localhost:8080/ocr/{job-id}'
    ```

- **Expected HTTP Response:** `200 OK`

- **Response Body Example:**

    ```json
    {
        "result": "Extracted text from the OCR processing..."
    }
    ```

Replace `{job-id}` with the actual ID returned from the upload response.