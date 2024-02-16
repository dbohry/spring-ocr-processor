FROM openjdk:17-jdk-slim as BuildJava

RUN apt-get update && \
    apt-get install -y tesseract-ocr wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir -p /usr/share/tesseract-ocr/4.00/tessdata/ && \
    wget https://github.com/tesseract-ocr/tessdata/raw/main/deu.traineddata -P /usr/share/tesseract-ocr/4.00/tessdata/

ARG JAR_FILE=build/libs/spring-ocr-tesseract-0.0.1.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]